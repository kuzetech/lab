package com.kuzetech.bigdata.spark.sql;

import com.kuzetech.bigdata.spark.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

import static org.apache.spark.sql.functions.col;

public class TestCreateDataFrameByBeanClass {
    public static void main(String[] args) {
        Dataset<Row> rows = SparkSessionUtils.generatePersonDataFrameByBeanClass();

        Dataset<Row> nameRDD = rows.select(col("name"));

        List<Row> rowList = nameRDD.collectAsList();

        for (Row row : rowList) {
            System.out.printf("name : %s %n", row.getString(0));
        }
    }
}
