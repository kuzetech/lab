package com.kuzetech.bigdata.flink;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class LaunchUtil {
    public static StreamTableEnvironment GetDefaultStreamTableEnvironment() {
        StreamExecutionEnvironment execEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        execEnvironment.setParallelism(1);
        return StreamTableEnvironment.create(execEnvironment);
    }
}
