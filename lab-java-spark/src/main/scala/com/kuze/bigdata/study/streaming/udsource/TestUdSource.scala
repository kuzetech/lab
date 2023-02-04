package com.kuze.bigdata.study.streaming.udsource

import java.util.concurrent.TimeUnit

import com.kuze.bigdata.study.utils.SparkSessionUtils
import org.apache.spark.sql.streaming.Trigger

object TestUdSource {

  def main(args: Array[String]): Unit = {

    val spark = SparkSessionUtils.initLocalSparkSession("TestUdSource")

    // 如果要使用简写 rate-sc 需要重新编译 spark 代码，因此就直接指定类的包路径
    val rateDF = spark.readStream
      .format("com.kuze.bigdata.study.streaming.udsource.ScRateStreamProvider")
      .option("rowsPerSecond", 3)
      .option("rampUpTime", "5s")
      .option("numPartitions", 3)
      .load

    rateDF
      .writeStream
      .format("console")
      .option("truncate", false)
      .trigger(Trigger.ProcessingTime(5, TimeUnit.SECONDS))
      .start()
      .awaitTermination()

  }

}
