package com.kuzetech.bigdata.study.streaming.outputModel;

import com.kuzetech.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;

import static org.apache.spark.sql.functions.*;



public class TestCompleteOutputModel {

    public static Dataset<Row> getRowDataset() {
        SparkSession session = SparkSessionUtils.initLocalSparkSession();

        Dataset<Row> socketDF = session.readStream()
                .format("socket")
                .option("host", "127.0.0.1")
                .option("port", 9999)
                .load();

        return socketDF
                .withColumn("words", split(col("value"), " "))
                .withColumn("word", explode(col("words")))
                .groupBy(col("word"))
                .count();
    }

    public static void main(String[] args) throws Exception {
        Dataset<Row> countDF = getRowDataset();

        // 输出全部行
        countDF.writeStream()
                .format("console")
                .option("truncate", false)
                .outputMode(OutputMode.Complete())
                .start()
                .awaitTermination();
    }
}
