package com.kuze.bigdata.study.core;

import com.kuze.bigdata.study.utils.SparkContextUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.LongAccumulator;
import scala.Tuple2;

import java.util.List;


public class TestAccumulator {
    public static void main(String[] args) {

        JavaSparkContext jsc = SparkContextUtils.initLocalJavaSparkContext("TestAccumulator");

        LongAccumulator accum = jsc.sc().longAccumulator();

        JavaRDD<String> wordRDD = SparkContextUtils.generateWordListJavaRDD(jsc);

        JavaPairRDD<String, Integer> pairRDD = wordRDD.mapToPair(o -> new Tuple2<String, Integer>(o, 1));

        JavaPairRDD<String, Integer> reduceRdd = pairRDD.reduceByKey((a, b) -> {
            accum.add(1);
            return a + b;
        });

        List<Tuple2<String, Integer>> result = reduceRdd.collect();

        System.out.printf("result size : %d %n", result.size());

        System.out.printf("accum : %d %n", accum.value());
    }
}
