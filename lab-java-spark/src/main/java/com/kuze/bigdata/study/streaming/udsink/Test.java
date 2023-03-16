package com.kuze.bigdata.study.streaming.udsink;

import org.apache.spark.sql.types.StructType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Test implements Serializable {

    private StructType structType;
    private List<String> availableServers;

    public List<String> getAvailableServers() {
        return availableServers;
    }

    public void setAvailableServers(List<String> availableServers) {
        this.availableServers = availableServers;
    }

    public StructType getStructType() {
        return structType;
    }

    public void setStructType(StructType structType) {
        this.structType = structType;
    }
}
