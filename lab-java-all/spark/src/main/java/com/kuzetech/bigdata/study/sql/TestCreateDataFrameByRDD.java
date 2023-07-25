package com.kuzetech.bigdata.study.sql;

import com.kuzetech.bigdata.study.utils.SparkSessionUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

public class TestCreateDataFrameByRDD {
    public static void main(String[] args) {
        Dataset<Row> rows = SparkSessionUtils.generatePersonDataFrameByStructType();

        List<Row> rowList = rows.collectAsList();

        for (Row row : rowList) {
            System.out.printf("name : %s , age : %d %n", row.getString(0), row.getInt(1));
        }
    }
}
