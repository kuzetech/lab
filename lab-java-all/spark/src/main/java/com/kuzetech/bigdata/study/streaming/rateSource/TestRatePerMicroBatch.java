package com.kuzetech.bigdata.study.streaming.rateSource;

import com.kuzetech.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class TestRatePerMicroBatch {
    public static void main(String[] args) throws Exception{

        SparkSession session = SparkSessionUtils.initLocalSparkSession();

        // 该 format 类型在 spark 3.3.0 刚加入
        Dataset<Row> rateDF = session.readStream().format("rate-micro-batch")
                .option("rowsPerBatch", 10)
                // 生成数据的开始时间
                .option("startTimestamp", 1662021667)
                // 每一批数据推进的时间
                .option("advanceMillisPerBatch", 1000)
                .option("numPartitions", 3)
                .load();

        rateDF.writeStream()
                .format("console")
                .option("truncate", false)
                .start()
                .awaitTermination();
    }
}