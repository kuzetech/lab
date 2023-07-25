package com.kuzetech.bigdata.study.streaming.outputModel;

import com.kuzetech.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;

import static org.apache.spark.sql.functions.*;


public class TestAppendOutputModel {
    public static void main(String[] args) throws Exception {
        SparkSession session = SparkSessionUtils.initLocalSparkSession();

        Dataset<Row> socketDF = session.readStream()
                .format("socket")
                .option("host", "127.0.0.1")
                .option("port", 9999)
                .load();

        Dataset<Row> resultDF = socketDF
                .withColumn("words", split(col("value"), " "))
                .withColumn("word", explode(col("words")));

        // 新行才会输出
        // 如果没有聚合操作是可以直接使用 Append 模式的
        resultDF.writeStream()
                .format("console")
                .option("truncate", false)
                .outputMode(OutputMode.Append())
                .start()
                .awaitTermination();
    }
}
