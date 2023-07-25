package com.kuzetech.bigdata.study.funnel;


import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

import static org.apache.spark.sql.types.DataTypes.*;

public class FunnelDataProcess {
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

        Map<String, Integer> stepMap = new HashMap<>();
        stepMap.put("#device_new-0", 1);
        stepMap.put("client_startup-3", 2);
        stepMap.put("client_startup-2", 3);
        stepMap.put("client_startup-17", 4);
        stepMap.put("client_startup-18", 5);
        stepMap.put("client_startup-4", 6);
        stepMap.put("client_startup-5", 7);
        stepMap.put("client_startup-6", 8);
        stepMap.put("client_startup-7", 8);
        stepMap.put("client_startup-8", 9);
        stepMap.put("client_startup-10", 10);
        stepMap.put("client_startup-11", 11);
        stepMap.put("client_startup-12", 12);
        stepMap.put("client_startup-13", 13);
        stepMap.put("client_startup-14", 14);
        stepMap.put("client_startup-15", 15);
        stepMap.put("client_startup-16", 16);
        stepMap.put("field-0", 17);

        Dataset<Row> sourceDF = spark.read().schema(structType).csv("/Users/huangsw/code/lab/lab-java-spark/data/yc_4y13.csv");

        StructType allStructType = new StructType(new StructField[]{
                DataTypes.createStructField("deviceId", StringType, false)
        });

        sourceDF.createTempView("events");

        Dataset<Row> deviceIdDF = spark.read().schema(allStructType).csv("/Users/huangsw/code/lab/lab-java-spark/data/first_step_deviceIds.csv");

        deviceIdDF.createTempView("deviceIds");

        Dataset<Row> joinDF = spark.sql("SELECT events.* FROM events INNER JOIN deviceIds ON events.deviceId = deviceIds.deviceId;");

        joinDF.cache();

        joinDF.printSchema();

        Dataset<String> distinctDF = joinDF.map(new MapFunction<Row, String>() {
            @Override
            public String call(Row v) throws Exception {
                return v.getString(v.fieldIndex("deviceId"));
            }
        }, Encoders.STRING());

        long count = distinctDF.distinct().count();

        System.out.println(count);

        Encoder<FunnyEvent> funnyEventEncoder = Encoders.bean(FunnyEvent.class);

        Dataset<FunnyEvent> funnyEventDF = joinDF.map(new MapFunction<Row, FunnyEvent>() {
            @Override
            public FunnyEvent call(Row v) throws Exception {
                return new FunnyEvent(
                        v.getString(v.fieldIndex("deviceId")),
                        v.getLong(v.fieldIndex("time")),
                        v.getString(v.fieldIndex("event")),
                        v.getInt(v.fieldIndex("step"))
                );
            }
        }, funnyEventEncoder);

        Encoder<Event> eventEncoder = Encoders.bean(Event.class);

        Dataset<Event> eventDF = funnyEventDF.map(new MapFunction<FunnyEvent, Event>() {
            @Override
            public Event call(FunnyEvent v) throws Exception {
                return new Event(
                        v.getDeviceId(),
                        v.getTime(),
                        stepMap.get(v.getKey())
                );
            }
        }, eventEncoder);

        Dataset<Event> validDF = eventDF
                .filter(eventDF.col("step").isNotNull())
                .filter(eventDF.col("time").isNotNull());

        MyAgg myAgg = new MyAgg();
        // Convert the function to a `TypedColumn` and give it a name
        TypedColumn<Event, Integer> aggLevel = myAgg.toColumn().name("level");

        Dataset<Tuple2<String, Integer>> result = validDF.groupByKey(new MapFunction<Event, String>() {
            @Override
            public String call(Event value) throws Exception {
                return value.getDeviceId();
            }
        }, Encoders.STRING()).agg(aggLevel);

        result.repartition(1).write().csv("/Users/huangsw/code/lab/lab-java-spark/data/level.csv");
    }
}
