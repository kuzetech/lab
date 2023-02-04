package com.kuze.bigdata.study.streaming.udsource

import java.io._
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

import org.apache.commons.io.IOUtils
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.util.DateTimeUtils
import org.apache.spark.sql.connector.read.{InputPartition, PartitionReader, PartitionReaderFactory}
import org.apache.spark.sql.connector.read.streaming.{MicroBatchStream, Offset}
import org.apache.spark.sql.execution.streaming._
import org.apache.spark.sql.execution.streaming.sources.{RateStreamMicroBatchInputPartition, RateStreamProvider}
import org.apache.spark.sql.util.CaseInsensitiveStringMap
import org.apache.spark.unsafe.types.UTF8String

class ScRateStreamMicroBatchStream(
                                    rowsPerSecond: Long,
                                    rampUpTimeSeconds: Long = 0,
                                    numPartitions: Int = 1,
                                    options: CaseInsensitiveStringMap,
                                    checkpointLocation: String)
  extends MicroBatchStream with Logging {

  import RateStreamProvider._

  val clock = {
    new SystemClock
  }

  private val maxSeconds = Long.MaxValue / rowsPerSecond

  if (rampUpTimeSeconds > maxSeconds) {
    throw new ArithmeticException("Integer overflow. " +
      s"Max offset with $rowsPerSecond rowsPerSecond" +
      s" is $maxSeconds, but 'rampUpTimeSeconds' is $rampUpTimeSeconds.")
  }

  val creationTimeMs = {
    val session = SparkSession.getActiveSession.orElse(SparkSession.getDefaultSession)
    require(session.isDefined)

    val metadataLog =
      new HDFSMetadataLog[LongOffset](session.get, checkpointLocation) {
        override def serialize(metadata: LongOffset, out: OutputStream): Unit = {
          val writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))
          writer.write("v" + VERSION + "\n")
          writer.write(metadata.json)
          writer.flush
        }

        override def deserialize(in: InputStream): LongOffset = {
          val content = IOUtils.toString(new InputStreamReader(in, StandardCharsets.UTF_8))
          // HDFSMetadataLog guarantees that it never creates a partial file.
          assert(content.length != 0)
          if (content(0) == 'v') {
            val indexOfNewLine = content.indexOf("\n")
            if (indexOfNewLine > 0) {
              MetadataVersionUtil.validateVersion(content.substring(0, indexOfNewLine), VERSION)
              LongOffset(SerializedOffset(content.substring(indexOfNewLine + 1)))
            } else {
              throw new IllegalStateException(
                s"Log file was malformed: failed to detect the log file version line.")
            }
          } else {
            throw new IllegalStateException(
              s"Log file was malformed: failed to detect the log file version line.")
          }
        }
      }

    metadataLog.get(0).getOrElse {
      val offset = LongOffset(clock.getTimeMillis())
      metadataLog.add(0, offset)
      logInfo(s"Start time: $offset")
      offset
    }.offset
  }

  @volatile private var lastTimeMs: Long = creationTimeMs

  override def initialOffset(): Offset = LongOffset(0L)

  override def latestOffset(): Offset = {
    val now = clock.getTimeMillis()
    if (lastTimeMs < now) {
      lastTimeMs = now
    }
    LongOffset(TimeUnit.MILLISECONDS.toSeconds(lastTimeMs - creationTimeMs))
  }

  override def deserializeOffset(json: String): Offset = {
    LongOffset(json.toLong)
  }


  override def planInputPartitions(start: Offset, end: Offset): Array[InputPartition] = {
    val startSeconds = start.asInstanceOf[LongOffset].offset
    val endSeconds = end.asInstanceOf[LongOffset].offset
    assert(startSeconds <= endSeconds, s"startSeconds($startSeconds) > endSeconds($endSeconds)")
    if (endSeconds > maxSeconds) {
      throw new ArithmeticException("Integer overflow. Max offset with " +
        s"$rowsPerSecond rowsPerSecond is $maxSeconds, but it's $endSeconds now.")
    }
    // Fix "lastTimeMs" for recovery
    if (lastTimeMs < TimeUnit.SECONDS.toMillis(endSeconds) + creationTimeMs) {
      lastTimeMs = TimeUnit.SECONDS.toMillis(endSeconds) + creationTimeMs
    }
    val rangeStart = valueAtSecond(startSeconds, rowsPerSecond, rampUpTimeSeconds)
    val rangeEnd = valueAtSecond(endSeconds, rowsPerSecond, rampUpTimeSeconds)
    logDebug(s"startSeconds: $startSeconds, endSeconds: $endSeconds, " +
      s"rangeStart: $rangeStart, rangeEnd: $rangeEnd")

    if (rangeStart == rangeEnd) {
      return Array.empty
    }

    val localStartTimeMs = creationTimeMs + TimeUnit.SECONDS.toMillis(startSeconds)
    val relativeMsPerValue =
      TimeUnit.SECONDS.toMillis(endSeconds - startSeconds).toDouble / (rangeEnd - rangeStart)

    (0 until numPartitions).map { p =>
      RateStreamMicroBatchInputPartition(
        p, numPartitions, rangeStart, rangeEnd, localStartTimeMs, relativeMsPerValue)
    }.toArray
  }

  override def createReaderFactory(): PartitionReaderFactory = {
    RateStreamMicroBatchReaderFactory
  }

  override def commit(end: Offset): Unit = {}

  override def stop(): Unit = {}

  override def toString: String = s"RateStreamV2[rowsPerSecond=$rowsPerSecond, " +
    s"rampUpTimeSeconds=$rampUpTimeSeconds, " +
    s"numPartitions=${options.getOrDefault(NUM_PARTITIONS, "default")}"
}

case class RateStreamMicroBatchInputPartition(
                                               partitionId: Int,
                                               numPartitions: Int,
                                               rangeStart: Long,
                                               rangeEnd: Long,
                                               localStartTimeMs: Long,
                                               relativeMsPerValue: Double) extends InputPartition

object RateStreamMicroBatchReaderFactory extends PartitionReaderFactory {
  override def createReader(partition: InputPartition): PartitionReader[InternalRow] = {
    val p = partition.asInstanceOf[RateStreamMicroBatchInputPartition]
    new RateStreamMicroBatchPartitionReader(p.partitionId, p.numPartitions, p.rangeStart,
      p.rangeEnd, p.localStartTimeMs, p.relativeMsPerValue)
  }
}

class RateStreamMicroBatchPartitionReader(
                                           partitionId: Int,
                                           numPartitions: Int,
                                           rangeStart: Long,
                                           rangeEnd: Long,
                                           localStartTimeMs: Long,
                                           relativeMsPerValue: Double) extends PartitionReader[InternalRow] {
  private var count: Long = 0


  override def next(): Boolean = {
    rangeStart + partitionId + numPartitions * count < rangeEnd
  }

  override def get(): InternalRow = {
    val currValue = rangeStart + partitionId + numPartitions * count
    count += 1
    val relative = math.round((currValue - rangeStart) * relativeMsPerValue)
    // 如果要写入 kafka ，字符串类型的数据必须使用 UTF8String
    // InternalRow(DateTimeUtils.millisToMicros(relative + localStartTimeMs), UTF8String.fromString(currValue.toString))
    InternalRow(UTF8String.fromString("login"), UTF8String.fromString("2022-01-01"), UTF8String.fromString(scala.util.Random.nextInt(100).toString))
  }

  override def close(): Unit = {}
}

