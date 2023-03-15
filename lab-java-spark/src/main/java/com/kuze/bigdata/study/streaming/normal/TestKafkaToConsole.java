package com.kuze.bigdata.study.streaming.normal;

import com.kuze.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class TestKafkaToConsole {
    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSessionUtils.initLocalSparkSession();

        Dataset<Row> kafkaDF = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", spark.conf().get("spark.kafka.bootstrap.servers"))
                .option("subscribe", "event")
                .option("startingOffsets", "latest")
                .option("group_id", "scala-first-try")
                .load();


        Dataset<Row> messageDF = kafkaDF.selectExpr("CAST(value AS STRING)");

        messageDF.writeStream()
                .format("console")
                .option("truncate", false)
                .option("checkpointLocation", "./checkpoint/scala-first-try")
                .start()
                .awaitTermination();
    }
}
