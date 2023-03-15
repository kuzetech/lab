package com.kuze.bigdata.study.streaming.partitionIndex;

import com.kuze.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class TestPartitionIndex {
    public static void main(String[] args) throws Exception{

        SparkSession spark = SparkSessionUtils.initLocalSparkSession();

        Dataset<Row> kafkaDF = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", spark.conf().get("spark.kafka.bootstrap.servers"))
                .option("subscribe", "event")
                .option("startingOffsets", "earliest")
                .option("group_id", "TestPartitionIndex-1")
                .load();

        Dataset<Row> messageDF = kafkaDF.selectExpr("CAST(value AS STRING)");

        Dataset<Row> repDF = messageDF.repartition(2);

        Dataset<Row> mapDF = repDF.mapPartitions(new MyMapPartitionsFunction(), Encoders.javaSerialization(Row.class));

        mapDF.writeStream()
                .format("console")
                .option("truncate", false)
                .start()
                .awaitTermination();

    }
}
