package com.kuzetech.bigdata.spark.streaming.updateBroadcast;

import org.apache.spark.sql.types.StructType;

import java.io.Serializable;
import java.util.List;

public class ClickhouseBroadcastContent implements Serializable {

    private StructType destTableStructType;
    private List<String> availableServer;

    public ClickhouseBroadcastContent(StructType destTableStructType, List<String> availableServer) {
        this.destTableStructType = destTableStructType;
        this.availableServer = availableServer;
    }

    public StructType getDestTableStructType() {
        return destTableStructType;
    }

    public void setDestTableStructType(StructType destTableStructType) {
        this.destTableStructType = destTableStructType;
    }

    public List<String> getAvailableServer() {
        return availableServer;
    }

    public void setAvailableServer(List<String> availableServer) {
        this.availableServer = availableServer;
    }
}
