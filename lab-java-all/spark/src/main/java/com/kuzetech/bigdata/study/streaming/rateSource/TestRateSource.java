package com.kuzetech.bigdata.study.streaming.rateSource;

import com.kuzetech.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.Trigger;

import java.util.concurrent.TimeUnit;

public class TestRateSource {
    public static void main(String[] args) throws Exception{

        SparkSession session = SparkSessionUtils.initLocalSparkSession();

        Dataset<Row> rateDF = session.readStream().format("rate")
                .option("rowsPerSecond", 3)
                 // 在生成速度变为 rowsPerSecond 之前需要多长时间
                .option("rampUpTime", "5s")
                .option("numPartitions", 3)
                .load();

        rateDF.writeStream()
                .format("console")
                .option("truncate", false)
                .trigger(Trigger.ProcessingTime(5, TimeUnit.SECONDS))
                .start()
                .awaitTermination();

        //生成数据的样式如下

        //-------------------------------------------
        //Batch: 3
        //-------------------------------------------
        //+-----------------------+-----+
        //|timestamp              |value|
        //+-----------------------+-----+
        //|2022-09-08 15:24:50.896|3    |
        //|2022-09-08 15:24:51.229|4    |
        //|2022-09-08 15:24:51.563|5    |
        //+-----------------------+-----+

    }
}