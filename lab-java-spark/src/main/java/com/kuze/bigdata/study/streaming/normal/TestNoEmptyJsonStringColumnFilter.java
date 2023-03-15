package com.kuze.bigdata.study.streaming.normal;

import com.kuze.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class TestNoEmptyJsonStringColumnFilter {

    public static void main(String[] args) throws Exception{

        SparkSession spark = SparkSessionUtils.initLocalSparkSession();

        Dataset<Row> kafkaDF = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", spark.conf().get("spark.kafka.bootstrap.servers"))
                .option("subscribe", "event")
                .option("startingOffsets", "earliest")
                .option("group_id", "TestNoEmptyJsonStringColumnFilter")
                .load();

        Dataset<Row> messageDF = kafkaDF.selectExpr("CAST(value AS STRING)");

        FilterFunction filterFunction = new NoEmptyJsonStringFilterFunction();

        Dataset<Row> validDF = messageDF.filter(filterFunction);

        validDF.writeStream()
                .format("console")
                .option("truncate", false)
                .start()
                .awaitTermination();

    }
}
