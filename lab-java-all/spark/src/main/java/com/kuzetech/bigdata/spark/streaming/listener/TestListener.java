package com.kuzetech.bigdata.spark.streaming.listener;

import com.kuzetech.bigdata.spark.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class TestListener {

    public static void main(String[] args) throws Exception{

        SparkSession spark = SparkSessionUtils.initLocalSparkSession();

        spark.streams().addListener(new MyStreamingQueryListener());

        Dataset<Row> kafkaDF = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", spark.conf().get("spark.kafka.bootstrap.servers"))
                .option("subscribe", "event")
                .option("startingOffsets", "earliest")
                .option("group_id", "TestListener")
                .load();

        Dataset<Row> messageDF = kafkaDF.selectExpr("CAST(value AS STRING)");

        messageDF.writeStream()
                .format("console")
                .option("truncate", false)
                .start()
                .awaitTermination();

    }
}
