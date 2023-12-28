package com.kuzetech.bigdata.spark;

import org.apache.spark.sql.RuntimeConfig;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;

public class AppTest {

    private static final SparkSession session = SparkSession.builder().appName("test").master("local[*]").getOrCreate();

    @Test
    public void test() {
        RuntimeConfig conf = session.conf();
        System.out.println(conf.getAll());
    }
}