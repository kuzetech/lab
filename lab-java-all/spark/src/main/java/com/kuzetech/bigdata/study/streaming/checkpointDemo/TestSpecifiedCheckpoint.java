package com.kuzetech.bigdata.study.streaming.checkpointDemo;

import com.kuzetech.bigdata.study.utils.ScalaToJavaUtils;
import com.kuzetech.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.connector.read.streaming.Offset;
import org.apache.spark.sql.execution.streaming.OffsetSeq;
import org.apache.spark.sql.execution.streaming.OffsetSeqLog;
import org.apache.spark.sql.streaming.OutputMode;
import scala.Option;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.spark.sql.types.DataTypes.StringType;

public class TestSpecifiedCheckpoint {

    public static void main(String[] args) throws Exception {
        SparkSession session = SparkSessionUtils.initLocalSparkSession();

        // 从指定的checkpoint目录中加载offset
        OffsetSeqLog offsetSeqLog = new OffsetSeqLog(session, "/Users/huangsw/code/lab/lab-java-spark/checkpoint/TestCheckpoint/offsets");

        String startingOffsets;
        if (offsetSeqLog.getLatestBatchId().isEmpty())
            startingOffsets = "earliest";
        else {
            OffsetSeq offsetSeq = offsetSeqLog.get((Long) offsetSeqLog.getLatestBatchId().get()).get();
            ScalaToJavaUtils<Option<Offset>> util = new ScalaToJavaUtils<>();
            List<Option<Offset>> list = util.convertSeqToList(offsetSeq.offsets());
            startingOffsets = list.stream().map(o -> o.get().json()).collect(Collectors.joining(","));
        }

        System.out.printf("获取到的最后偏移量为: %s %n", startingOffsets);

        // 最终的效果是每一次启动都会重新生成一个 checkpoint 文件夹
        // 然后从指定的 offset 开始消费

        Dataset<Row> kafkaDF = session.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", session.conf().get("spark.kafka.bootstrap.servers"))
                .option("subscribe", "test")
                .option("startingOffsets", startingOffsets)
                .load();

        Dataset<Row> messageDF = kafkaDF.withColumn("value", kafkaDF.col("value").cast(StringType));

        messageDF.writeStream()
                .format("console")
                .option("truncate", "false")
                .outputMode(OutputMode.Append())
                .start()
                .awaitTermination();
    }
}
