package com.kuzetech.bigdata.spark.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SparkContextUtils {

    public static JavaSparkContext initLocalJavaSparkContext() {
        SparkConf conf = SparkConfUtils.initSparkConf();

        JavaSparkContext sc = new JavaSparkContext(conf);
        return sc;
    }

    public static JavaRDD<String> generateWordListJavaRDD(JavaSparkContext javaSc) {
        JavaRDD<String> sourceRDD = javaSc.parallelize(Constants.wordList);

        return sourceRDD;
    }


    public static JavaRDD<String> generateWordListJavaRDD() {
        JavaSparkContext javaSc = initLocalJavaSparkContext();

        JavaRDD<String> sourceRDD = javaSc.parallelize(Constants.wordList);

        return sourceRDD;
    }

}
