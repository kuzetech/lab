package com.kuzetech.bigdata.spark.streaming.orderByOrSort;

import com.kuzetech.bigdata.spark.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.types.DataTypes;

import java.util.HashMap;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;

public class TestOrderBy {

    // 结论在 https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#unsupported-operations
    // Sorting operations are supported on streaming Datasets only after an aggregation and in Complete Output Mode.

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
                // 无论是 orderBy 或者 sortWithinPartitions 都会报错，如下：
                // Sorting is not supported on streaming DataFrames/Datasets,
                // unless it is on aggregated DataFrame/Dataset in Complete output mode;
                .orderBy(col("uid").asc());

        resultDF.writeStream()
                .format("console")
                .option("truncate", false)
                .outputMode(OutputMode.Append())
                .start()
                .awaitTermination();
    }
}
