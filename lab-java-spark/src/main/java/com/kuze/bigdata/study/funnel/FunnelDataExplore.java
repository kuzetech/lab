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

        StructType allStructType = new StructType(new StructField[]{
                DataTypes.createStructField("deviceId", StringType, false),
                DataTypes.createStructField("time", LongType, false),
                DataTypes.createStructField("event", StringType, false),
                DataTypes.createStructField("step", IntegerType, false),
        });

        Dataset<Row> sourceDF = spark.read().schema(allStructType).csv("/Users/huangsw/code/lab/lab-java-spark/data/yc_4y13.csv");

        Dataset<Row> validDF = sourceDF.filter(new FilterFunction<Row>() {
            @Override
            public boolean call(Row v) throws Exception {
                String event = v.getString(v.fieldIndex("event"));
                if (!event.trim().equalsIgnoreCase("#device_new")) {
                    return false;
                }
                Long time = v.getLong(v.fieldIndex("time"));
                if (time.longValue() < 1681315200000L || time.longValue() > 1681401599999L) {
                    return false;
                }
                return true;
            }
        });

        Dataset<String> deviceIdDF = validDF.map(new MapFunction<Row, String>() {
            @Override
            public String call(Row v) throws Exception {
                return v.getString(v.fieldIndex("deviceId"));
            }
        }, Encoders.STRING());

        deviceIdDF.cache();

        // 全事件设备数量 = 854
        // #device_new 设备数量 = 844
        long count = deviceIdDF.distinct().count();
        System.out.println(count);


        // deviceIdDF.repartition(1).write().csv("/Users/huangsw/code/lab/lab-java-spark/data/first_step_deviceIds.csv");


    }
}
