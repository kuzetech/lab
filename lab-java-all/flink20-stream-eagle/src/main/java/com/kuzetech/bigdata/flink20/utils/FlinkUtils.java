package com.kuzetech.bigdata.flink20.utils;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkUtils {
    public static final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    public static StreamExecutionEnvironment initEnv(ParameterTool parameterTool) {
        return env;
    }
}
 