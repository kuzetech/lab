package com.kuze.bigdata.study.utils;

import org.apache.spark.SparkConf;

public class SparkConfUtils {

    public static SparkConf initSparkConf() {
        SparkConf conf = new SparkConf();

        if (conf.getOption("spark.master").isEmpty()) {
            conf.setMaster("local[2]");
            conf.set("spark.driver.bindAddress", "127.0.0.1");
            System.setProperty("HADOOP_USER_NAME", "hdfsuser");
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
