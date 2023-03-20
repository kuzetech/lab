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
        // {"uid":8,"eventId":"test","eventTime":"2022-01-01"}

        Dataset<Row> kafkaDF = session.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "broker:29092")
                .option("subscribe", "test")
                .option("startingOffsets", "latest")
                .load();

        Dataset<Row> resultDF = kafkaDF
                .withColumn("value", col("value").cast(DataTypes.StringType))
                .withColumn("value", from_json(col("value"), "eventTime Date,eventId string,uid int", new HashMap<String, String>()))
                .select(col("value.*"));

        resultDF.writeStream()
                .outputMode(OutputMode.Append())
                .format("com.kuze.bigdata.study.streaming.udsink.ClickHouseStreamSinkProvider")
                .option("checkpointLocation", "/TestUDSink")
                .option("connectUrl", "jdbc:clickhouse://clickhouse1:8123,clickhouse2:8123/system")
                .option("cluster", "my")
                .option("port", "8123")
                .option("user", "default")
                .option("password", "")
                .option("database", "default")
                .option("table", "event_local")
                .option("shardingColumn", "uid")
                .option("fsParentLocation", "/TestUDSink")
                .start()
                .awaitTermination();
    }
}
