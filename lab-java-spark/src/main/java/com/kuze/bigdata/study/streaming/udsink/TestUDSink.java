package com.kuze.bigdata.study.streaming.udsink;

import com.kuze.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.types.DataTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;

public class TestUDSink {

    private final static Logger logger = LoggerFactory.getLogger(TestUDSink.class);

    public static void main(String[] args) throws Exception {
        SparkSession session = SparkSessionUtils.initLocalSparkSession();

        // 执行命令 nc -lk 9999 输入数据如下：
        // {"uid":1,"eventId":"test","eventTime":"2022-01-01"}
        Dataset<Row> socketDF = session.readStream()
                .format("socket")
                .option("host", "127.0.0.1")
                .option("port", 9999)
                .load();

        Dataset<Row> resultDF = socketDF
                .withColumn("value", col("value").cast(DataTypes.StringType))
                .withColumn("value", from_json(col("value"), "uid int,eventId string, eventTime Date", new HashMap<String, String>()))
                .select(col("value.*"));

        resultDF.writeStream()
                .outputMode(OutputMode.Append())
                .format("com.kuze.bigdata.study.streaming.udsink.ClickHouseStreamSinkProvider")
                .option("checkpointLocation", "/Users/kuze/code/lab/lab-java-spark/checkpoint/TestUDSink")
                .option("connectUrl", "jdbc:clickhouse://172.21.0.9:8123,172.21.0.10:8123/system")
                .option("cluster", "my")
                .option("port", "8123")
                .option("user", "default")
                .option("password", "")
                .option("database", "default")
                .option("table", "event_local")
                .option("shardingColumn", "uid")
                .start()
                .awaitTermination();
    }
}
