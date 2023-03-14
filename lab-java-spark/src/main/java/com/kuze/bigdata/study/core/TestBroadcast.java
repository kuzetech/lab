package com.kuze.bigdata.study.core;

import com.kuze.bigdata.study.utils.SparkContextUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;

import java.util.ArrayList;
import java.util.List;

public class TestBroadcast {
    public static void main(String[] args) {

        JavaSparkContext jsc = SparkContextUtils.initLocalJavaSparkContext("TestBroadcast");

        List<String> availableWordList = new ArrayList<>();
        availableWordList.add("a");
        availableWordList.add("e");

        Broadcast<List<String>> availableWordBroadcast = jsc.broadcast(availableWordList);

        JavaRDD<String> wordRDD = SparkContextUtils.generateWordListJavaRDD(jsc);

        JavaRDD<String> filterRDD = wordRDD.filter(w -> {
            List<String> awl = availableWordBroadcast.value();
            if (awl.contains(w)) {
                return true;
            } else {
                return false;
            }
        });

        List<String> result = filterRDD.collect();

        result.forEach(o->{
            System.out.printf("word : %s %n", o);
        });

    }
}
