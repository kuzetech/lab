package com.kuzetech.bigdata.spark.core;

import com.kuzetech.bigdata.spark.utils.SparkContextUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.util.List;


public class TestAggregateByKey {
    public static void main(String[] args) {
        JavaRDD<String> wordRDD = SparkContextUtils.generateWordListJavaRDD();

        JavaPairRDD<String, Integer> pairs = wordRDD.mapToPair(w -> new Tuple2(w, 1));

        // JavaPairRDD<String, Integer> reduceRdd = pairs.reduceByKey((a, b) -> a + b);

        JavaPairRDD<String, Integer> aggRDD = pairs.aggregateByKey(0, (a, b) -> a + b, (a, b) -> a + b);

        List<Tuple2<String, Integer>> result = aggRDD.collect();

        result.forEach(o -> {
            System.out.printf("word : %s , count : %d %n", o._1, o._2);
        });
    }
}
