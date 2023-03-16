package com.kuze.bigdata.study.streaming.udsink;

import org.apache.spark.sql.types.StructType;

import java.io.Serializable;

public class Test implements Serializable {

    private StructType structType;

    public StructType getStructType() {
        return structType;
    }

    public void setStructType(StructType structType) {
        this.structType = structType;
    }
}
