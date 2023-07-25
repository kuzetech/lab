package com.kuzetech.bigdata.study.streaming.outputModel;

import com.kuzetech.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.types.DataTypes;

import static org.apache.spark.sql.functions.*;


public class TestAppendAggOutputModel {
    public static void main(String[] args) throws Exception {
        SparkSession session = SparkSessionUtils.initLocalSparkSession();


        // 执行命令 nc -lk 9999 输入数据如下：
        // 2023-03-16 10:45:00,Apache Spark
        // 2023-03-16 10:46:00,Spark Logo
        // 2023-03-16 10:52:00,Structured Streaming
        // 2023-03-16 10:57:00,Spark Streaming
        // 2023-03-16 11:01:00,Spark Streaming
        // 2023-03-16 11:02:00,Spark Streaming
        Dataset<Row> socketDF = session.readStream()
                .format("socket")
                .option("host", "127.0.0.1")
                .option("port", 9999)
                .load();

        Dataset<Row> resultDF = socketDF
                .withColumn("inputs", split(col("value"), ","))
                // Event time must be defined timestamp
                .withColumn("eventTime", element_at(col("inputs"), 1).cast(DataTypes.TimestampType))
                .withColumn("words", split(element_at(col("inputs"), 2).cast(DataTypes.StringType), " "))
                .withColumn("word", explode(col("words")))
                .withWatermark("eventTime", "10 minute")
                .groupBy(window(col("eventTime"), "5 minute"),col("word"))
                .count();

        // 新行才会输出
        // 如果有聚合操作，直接使用 Append 模式会报错
        // Append output mode not supported when there are streaming aggregations on streaming DataFrames/DataSets without watermark
        // 当窗口关闭才会一次性输出窗口内的数据
        resultDF.writeStream()
                .format("console")
                .option("truncate", false)
                .outputMode(OutputMode.Complete())
                .start()
                .awaitTermination();
    }
}
