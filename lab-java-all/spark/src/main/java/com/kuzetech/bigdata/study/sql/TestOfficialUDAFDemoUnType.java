package com.kuzetech.bigdata.study.sql;

import org.apache.spark.sql.*;

public class TestOfficialUDAFDemoUnType {
    public static void main(String[] args) {

        SparkSession spark = SparkSession.builder()
                .appName("Java UDAF bbbca")
                .master("local[*]")
                .getOrCreate();

        spark.udf().register("myAverage", functions.udaf(new MyAverageLong(), Encoders.LONG()));

        Dataset<Row> df = spark.read().json("/Users/huangsw/code/lab/lab-java-spark/data/salary.json");
        df.createOrReplaceTempView("employees");

        Dataset<Row> result = spark.sql("SELECT name, myAverage(salary) as average_salary FROM employees group by name");
        result.show();

    }
}
