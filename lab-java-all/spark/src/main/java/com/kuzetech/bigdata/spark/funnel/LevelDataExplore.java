package com.kuzetech.bigdata.spark.funnel;


import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import static org.apache.spark.sql.types.DataTypes.IntegerType;
import static org.apache.spark.sql.types.DataTypes.StringType;

public class LevelDataExplore {
    public static void main(String[] args) throws Exception{
        // 创建SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("FunnelAnalysis")
                .master("local[*]")
                .getOrCreate();

        StructType allStructType = new StructType(new StructField[]{
                DataTypes.createStructField("deviceId", StringType, false),
                DataTypes.createStructField("step", IntegerType, false),
        });

        Dataset<Row> sourceDF = spark.read().schema(allStructType).csv("/Users/huangsw/code/lab/lab-java-spark/data/level.csv");

        Dataset<String> deviceIdDF = sourceDF.map(new MapFunction<Row, String>() {
            @Override
            public String call(Row v) throws Exception {
                return v.getString(v.fieldIndex("deviceId"));
            }
        }, Encoders.STRING());


        long count = deviceIdDF.distinct().count();
        System.out.println(count);

    }
}
