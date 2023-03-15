package com.kuze.bigdata.study.streaming.checkpoint;

import com.kuze.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;

import static org.apache.spark.sql.types.DataTypes.StringType;

public class TestCheckpoint {

    public static void main(String[] args) throws Exception {
        SparkSession session = SparkSessionUtils.initLocalSparkSession();

        Dataset<Row> kafkaDF = session.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", session.conf().get("spark.kafka.bootstrap.servers"))
                .option("subscribe", "test")
                .option("startingOffsets", "latest")
                .load();

        Dataset<Row> messageDF = kafkaDF.withColumn("value", kafkaDF.col("value").cast(StringType));

        /*Dataset<Row> testDF = messageDF.map(new MapFunction<Row, Row>() {
            @Override
            public Row call(Row value) throws Exception {
                int index = value.fieldIndex("value");
                String valueColumnValue = value.getString(index);
                if(valueColumnValue.length() > 3){
                    throw new Exception("test");
                }
                return value;
            }
        }, ExpressionEncoder.javaBean(Row.class));*/

        // 通过 option 的方式配置，异常重启启动后会自动重放最后一批数据
        messageDF.writeStream()
                .format("console")
                .option("truncate", "false")
                .option("checkpointLocation", "/Users/huangsw/code/lab/lab-java-spark/ckdata//TestCheckpoint")
                .outputMode(OutputMode.Append())
                .start()
                .awaitTermination();
    }
}
