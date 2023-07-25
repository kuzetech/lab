package com.kuzetech.bigdata.study.streaming.foreachbatch;

import com.kuzetech.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.TaskContext;
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

public class TestForeachBatch {

    private final static Logger logger = LoggerFactory.getLogger(TestForeachBatch.class);

    public static void main(String[] args) throws Exception {
        SparkSession session = SparkSessionUtils.initLocalSparkSession();

        // 执行命令 nc -lk 9999 输入数据如下：
        // {"uid":1,"eventTime":"2022-01-01"}
        // {"uid":2,"eventTime":"2022-01-01"}
        // {"uid":3,"eventTime":"2022-01-01"}
        Dataset<Row> socketDF = session.readStream()
                .format("socket")
                .option("host", "127.0.0.1")
                .option("port", 9999)
                .load();

        Dataset<Row> resultDF = socketDF
                .withColumn("value", col("value").cast(DataTypes.StringType))
                .withColumn("value", from_json(col("value"), "uid int, eventTime Date", new HashMap<>()))
                .select(col("value.*"))
                .repartition(3, col("uid"));

        resultDF.writeStream()
                .outputMode(OutputMode.Append())
                .foreachBatch((batch, batchId) -> {
                    logger.error("这里在 driver 执行，仅一次");
                    batch.foreachPartition(iter -> {
                        logger.error("这里在 executor 执行，仅一次");
                        int partitionId = TaskContext.getPartitionId();
                        while (iter.hasNext()) {
                            Row row = iter.next();
                            logger.error(String.format("分区 %d，传入的值为 %s", partitionId, row.mkString()));
                        }
                    });
                })
                .start()
                .awaitTermination();
    }
}
