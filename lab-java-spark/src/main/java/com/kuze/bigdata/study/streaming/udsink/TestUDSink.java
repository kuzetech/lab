package com.kuze.bigdata.study.streaming.udsink;

import com.kuze.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;

import static org.apache.spark.sql.functions.col;

public class TestUDSink {

    public static void main(String[] args) throws Exception{
        SparkSession spark = SparkSessionUtils.initLocalSparkSession();

        Dataset<Row> kafkaDF = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", spark.conf().get("spark.kafka.bootstrap.servers"))
                .option("subscribe", "event")
                .option("startingOffsets", "earliest")
                .load();

        Dataset<Row> messageDF = kafkaDF.selectExpr("CAST(value AS STRING)");

        Dataset<Row> tableDF = messageDF.selectExpr("from_json(value, 'uid String, eventTime Date') as value").select(col("value.*"));

        // 这里会报错，不允许 append 类型的 sort 算子
        // Sorting is not supported on streaming DataFrames/Datasets, unless it is on aggregated DataFrame/Dataset in Complete output mode
        Dataset<Row> sortDF = tableDF.sortWithinPartitions(col("eventTime"));

        sortDF.writeStream()
                .outputMode(OutputMode.Append())
                .option("checkpointLocation", "/Users/huangsw/code/study/study-spark/checkpoint")
                .format("com.kuze.bigdata.study.streaming.udsink.MyStreamSinkProvider")
                .start()
                .awaitTermination();
    }
}
