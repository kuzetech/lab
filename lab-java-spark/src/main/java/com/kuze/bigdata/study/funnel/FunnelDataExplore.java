package com.kuze.bigdata.study.funnel;


import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import static org.apache.spark.sql.types.DataTypes.*;

public class FunnelDataExplore {
    public static void main(String[] args) throws Exception{
        // 创建SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("FunnelAnalysis")
                .master("local[*]")
                .getOrCreate();

        StructType structType = new StructType(new StructField[]{
                DataTypes.createStructField("deviceId", StringType, false),
                DataTypes.createStructField("time", LongType, false),
                DataTypes.createStructField("event", StringType, false),
                DataTypes.createStructField("step", IntegerType, false),
        });

        Encoder<FunnyEvent> funnyEventEncoder = Encoders.bean(FunnyEvent.class);

        Dataset<Row> data = spark.read().schema(structType).csv("/Users/huangsw/code/lab/lab-java-spark/data/yc_4y13.csv");

        Dataset<Row> firstDF = data.filter(new FilterFunction<Row>() {
            @Override
            public boolean call(Row v) throws Exception {
                String event = v.getString(v.fieldIndex("event"));
                if (event == "#device_new") {
                    return true;
                }
                return false;
            }
        });

        Dataset<String> deviceIdDF = firstDF.map(new MapFunction<Row, String>() {
            @Override
            public String call(Row v) throws Exception {
                return v.getString(v.fieldIndex("deviceId"));
            }
        }, Encoders.STRING());

        // 854
        long count = deviceIdDF.distinct().count();
        System.out.println(count);


    }
}
