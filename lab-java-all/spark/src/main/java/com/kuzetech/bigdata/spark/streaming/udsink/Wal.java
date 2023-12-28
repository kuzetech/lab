package com.kuzetech.bigdata.spark.streaming.udsink;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;
import java.util.List;

public class Wal implements Serializable {

    private List<String> batchAllocation;
    private String batchIndex;
    @JsonIgnore
    private StructType structType;

    public StructType getStructType() {
        return structType;
    }

    public void setStructType(StructType structType) {
        this.structType = structType;
    }

    public List<String> getBatchAllocation() {
        return batchAllocation;
    }

    public void setBatchAllocation(List<String> batchAllocation) {
        this.batchAllocation = batchAllocation;
    }

    public String getBatchIndex() {
        return batchIndex;
    }

    public void setBatchIndex(String batchIndex) {
        this.batchIndex = batchIndex;
    }

    @Override
    public String toString() {
        return "Wal{" +
                "batchAllocation=" + batchAllocation +
                ", batchIndex='" + batchIndex + '\'' +
                ", structType=" + structType +
                '}';
    }
}
