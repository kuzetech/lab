package com.kuze.bigdata.study.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.SparkSession;
import scala.reflect.ClassTag;

import java.util.ArrayList;
import java.util.List;

public class SparkSessionUtils {

    public static final List<String> wordList = new ArrayList<>();
    static {
        wordList.add("a");
        wordList.add("b");
        wordList.add("c");
        wordList.add("d");
        wordList.add("e");
        wordList.add("");
    }

    public static SparkSession initLocalSparkSession(String appName){
        SparkConf conf = new SparkConf();
        conf.setMaster("local[*]");

        SparkSession spark = SparkSession
                .builder()
                .appName(appName)
                .config(conf)
                .getOrCreate();

        return spark;
    }


    public static RDD<String> generateWordListRdd(String appName){
        SparkSession spark = initLocalSparkSession(appName);

        JavaToScalaUtils<String> utils = new JavaToScalaUtils<>();

        RDD<String> distData = spark.sparkContext().parallelize(
                utils.convertJavaListToScalaSet(wordList),
                3,
                ClassTag.apply(String.class));

        return distData;
    }

}
