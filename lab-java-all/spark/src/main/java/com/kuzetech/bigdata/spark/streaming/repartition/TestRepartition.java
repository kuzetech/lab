package com.kuzetech.bigdata.spark.streaming.repartition;

import com.kuzetech.bigdata.spark.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.types.DataTypes;

import java.util.HashMap;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;

public class TestRepartition {

    public static void main(String[] args) throws Exception{
        SparkSession session = SparkSessionUtils.initLocalSparkSession();

        // 执行命令 nc -lk 9999 输入数据如下：
        // {"uid":1,"eventTime":"2022-01-01"}
        // {"uid":1,"eventTime":"2022-01-01"}
        Dataset<Row> socketDF = session.readStream()
                .format("socket")
                .option("host", "127.0.0.1")
                .option("port", 9999)
                .load();

        Dataset<Row> resultDF = socketDF
                .withColumn("value", col("value").cast(DataTypes.StringType))
                .withColumn("value", from_json(col("value"), "uid int, eventTime Date", new HashMap<>()))
                .select(col("value.*"))
                // 按字段的范围分区的
                // .repartitionByRange()
                // 按字段的哈希值分区的
                .repartition(2, col("uid"));

        resultDF.writeStream()
                .format("console")
                .option("truncate", false)
                .outputMode(OutputMode.Append())
                .start()
                .awaitTermination();
    }
}
