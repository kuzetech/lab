package com.kuzetech.bigdata.spark.sql;

import org.apache.spark.sql.*;

public class TestOfficialUDAFDemoUnTypeList {
    public static void main(String[] args) {

        SparkSession spark = SparkSession.builder()
                .appName("Java UDAF agaahab")
                .master("local[*]")
                .getOrCreate();

        spark.udf().register("myAverage", functions.udaf(new MyAverageList(), Encoders.LONG()));

        Dataset<Row> df = spark.read().json("/Users/huangsw/code/lab/lab-java-spark/data/salary.json");
        df.createOrReplaceTempView("employees");

        Dataset<Row> result = spark.sql("SELECT name, myAverage(salary) as average_salary FROM employees group by name");
        result.show();

    }
}
