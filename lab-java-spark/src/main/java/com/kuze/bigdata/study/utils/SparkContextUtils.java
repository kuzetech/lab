package com.kuze.bigdata.study.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SparkContextUtils {

    public static JavaSparkContext initLocalJavaSparkContext(String appName) {
        SparkConf conf = new SparkConf();
        conf.setAppName(appName);
        conf.setMaster("local[*]");

        JavaSparkContext sc = new JavaSparkContext(conf);
        return sc;
    }

    public static JavaRDD<String> generateWordListJavaRDD(JavaSparkContext javaSc) {
        JavaRDD<String> sourceRDD = javaSc.parallelize(Constants.wordList);

        return sourceRDD;
    }


    public static JavaRDD<String> generateWordListJavaRDD(String appName) {
        JavaSparkContext javaSc = initLocalJavaSparkContext(appName);

        JavaRDD<String> sourceRDD = javaSc.parallelize(Constants.wordList);

        return sourceRDD;
    }

}
