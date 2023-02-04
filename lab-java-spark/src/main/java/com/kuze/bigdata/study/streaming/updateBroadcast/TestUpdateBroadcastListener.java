package com.kuze.bigdata.study.streaming.updateBroadcast;

import com.kuze.bigdata.study.clickhouse.ClickhouseQueryService;
import com.kuze.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class TestUpdateBroadcastListener {

    public static void main(String[] args) throws Exception{

        SparkSession spark = SparkSessionUtils.initLocalSparkSession("TestUpdateBroadcastListener");

        ClickhouseQueryService chService = new ClickhouseQueryService();
        LoadResourceManager loadResourceManager = new LoadResourceManager();
        UpdateBroadcastListener listener = new UpdateBroadcastListener(spark, loadResourceManager, chService);
        spark.streams().addListener(listener);

        Dataset<Row> kafkaDF = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", "event")
                .option("startingOffsets", "earliest")
                .option("group_id", "TestUpdateBroadcastListener")
                .load();

        Dataset<Row> messageDF = kafkaDF.selectExpr("CAST(value AS STRING)");

        Dataset<Row> filterDF = messageDF.filter(new FilterFunction<Row>() {
            @Override
            public boolean call(Row value) throws Exception {
                ClickhouseBroadcastContent broadcastContent = loadResourceManager.get().value();
                System.out.println(broadcastContent);
                return true;
            }
        });

        filterDF.writeStream()
                .format("console")
                .option("truncate", false)
                .start()
                .awaitTermination();

    }
}
