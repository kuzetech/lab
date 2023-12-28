package com.kuzetech.bigdata.spark.streaming.partitionIndex;

import com.kuzetech.bigdata.spark.utils.SparkSessionUtils;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestPartitionIndex {
    public static void main(String[] args) throws Exception{

        SparkSession spark = SparkSessionUtils.initLocalSparkSession();

        Dataset<Row> kafkaDF = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", spark.conf().get("spark.kafka.bootstrap.servers"))
                .option("subscribe", "pass")
                .option("startingOffsets", "earliest")
                .load();

        Dataset<Row> messageDF = kafkaDF.withColumn("value", kafkaDF.col("value").cast(DataTypes.StringType));

        Dataset<Row> repDF = messageDF.repartition(2);

        Dataset<String> mapDF = repDF.mapPartitions(new MapPartitionsFunction<Row, String>() {
            @Override
            public Iterator<String> call(Iterator<Row> input) throws Exception {
                int partitionId = TaskContext.getPartitionId();
                System.out.printf("当前分区ID为：%d %n", partitionId);
                List<String> list = new ArrayList<>();
                while (input.hasNext()){
                    Row row = input.next();
                    int index = row.fieldIndex("value");
                    list.add(row.getString(index));
                }
                return list.iterator();
            }
        }, Encoders.STRING());

        mapDF.writeStream()
                .format("console")
                .option("truncate", false)
                .start()
                .awaitTermination();

    }
}
