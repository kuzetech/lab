package com.kuzetech.bigdata.spark.streaming.multipleStreaming;

import com.kuzetech.bigdata.spark.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import static org.apache.spark.sql.functions.col;

/**
 * 最终结果 ：只有第一条流成功执行了
 */
public class TestMultipleStreaming {

    public static void main(String[] args) throws Exception{
        SparkSession spark = SparkSessionUtils.initLocalSparkSession();

        Dataset<Row> kafkaDF = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", spark.conf().get("spark.kafka.bootstrap.servers"))
                .option("subscribe", "event")
                .option("startingOffsets", "earliest")
                .option("group_id", "TestMultipleStreaming")
                .load();

        Dataset<Row> messageDF = kafkaDF.selectExpr("CAST(value AS STRING)");

        Dataset<Row> tableDF = messageDF.selectExpr("from_json(value, 'uid String, eventId String, eventTime Date') as value").select(col("value.*"));

        tableDF.writeStream()
                .format("console")
                .option("truncate", false)
                .start()
                .awaitTermination();

        Dataset<Row> kafkaDF2 = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", spark.conf().get("spark.kafka.bootstrap.servers"))
                .option("subscribe", "event2")
                .option("startingOffsets", "earliest")
                .option("group_id", "TestMultipleStreaming")
                .load();

        Dataset<Row> messageDF2 = kafkaDF2.selectExpr("CAST(value AS STRING)");

        Dataset<Row> tableDF2 = messageDF2.selectExpr("from_json(value, 'uid String, eventId String, eventTime Date') as value").select(col("value.*"));


        tableDF2.writeStream()
                .format("console")
                .option("truncate", false)
                .start()
                .awaitTermination();
    }
}
