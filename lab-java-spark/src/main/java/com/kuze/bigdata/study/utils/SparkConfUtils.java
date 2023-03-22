package com.kuze.bigdata.study.utils;

import org.apache.spark.SparkConf;

public class SparkConfUtils {

    public static SparkConf initSparkConf() {
        System.setProperty("HADOOP_USER_NAME", "hdfsuser");

        SparkConf conf = new SparkConf();
        //conf.setMaster("spark://172.18.0.8:7077");

        if (conf.getOption("spark.master").isEmpty()) {
            conf.setMaster("local[2]");
            conf.set("spark.driver.bindAddress", "localhost");
        }

        if (conf.getOption("spark.app.name").isEmpty()) {
            conf.setAppName("test");
        }

        /*// 设置checkpoint当前存放在本地的指定位置
        if (conf.getOption("spark.sql.streaming.checkpointLocation").isEmpty()) {
            conf.set("spark.sql.streaming.checkpointLocation", "/Users/huangsw/code/lab/lab-java-spark/checkpoint");
        }*/

        return conf;
    }

}
