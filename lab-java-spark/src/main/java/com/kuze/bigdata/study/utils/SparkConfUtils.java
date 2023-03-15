package com.kuze.bigdata.study.utils;

import org.apache.spark.SparkConf;

public class SparkConfUtils {

    public static SparkConf initSparkConf() {
        SparkConf conf = new SparkConf();

        if (conf.getOption("spark.master").isEmpty()) {
            conf.setMaster("local[2]");
        }

        if (conf.getOption("spark.app.name").isEmpty()) {
            conf.setAppName("test");
        }

        // 设置checkpoint当前存放在本地的指定位置
        if (conf.getOption("spark.sql.streaming.checkpointLocation").isEmpty()) {
            conf.set("spark.sql.streaming.checkpointLocation", "/Users/huangsw/code/lab/lab-java-spark/checkpoint");
        }

        /**
         * Kafka服务器相关配置
         */
        String kafkaBootstrapServers = "localhost:9092";
        if (conf.getOption("spark.kafka.bootstrap.servers").isEmpty()) {
            // kafkaBootstrapServers = conf.get("spark.kafka.bootstrap.servers");
            conf.set("spark.kafka.bootstrap.servers", kafkaBootstrapServers);
        }

        return conf;
    }

}
