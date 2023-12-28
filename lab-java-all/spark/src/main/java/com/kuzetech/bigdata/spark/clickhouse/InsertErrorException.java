package com.kuzetech.bigdata.spark.clickhouse;

public class InsertErrorException extends Exception {

    public InsertErrorException(String msg) {
        super(msg);
    }
}
