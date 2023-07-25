package com.kuzetech.bigdata.study.sql;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import scala.Tuple2;

public class TestOfficialUDAFDemo {
    public static void main(String[] args) {

        SparkSession spark = SparkSession.builder()
                .appName("Java UDAF dddd")
                .master("local[*]")
                .getOrCreate();

        Encoder<Employee> employeeEncoder = Encoders.bean(Employee.class);
        String path = "/Users/huangsw/code/lab/lab-java-spark/data/salary.json";
        Dataset<Employee> df = spark.read().json(path).as(employeeEncoder);

        MyAverage myAverage = new MyAverage();
        // Convert the function to a `TypedColumn` and give it a name
        TypedColumn<Employee, Double> averageSalary = myAverage.toColumn().name("average_salary");

        Dataset<Tuple2<String, Double>> result = df.groupByKey(new MapFunction<Employee, String>() {
            @Override
            public String call(Employee value) throws Exception {
                return value.getName();
            }
        }, Encoders.STRING()).agg(averageSalary);

        result.show();
    }
}
