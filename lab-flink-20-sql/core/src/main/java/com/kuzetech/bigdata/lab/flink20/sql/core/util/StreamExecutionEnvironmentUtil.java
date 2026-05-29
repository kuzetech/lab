package com.kuzetech.bigdata.lab.flink20.sql.core.util;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static org.apache.flink.configuration.RestOptions.BIND_PORT;

public class StreamExecutionEnvironmentUtil {
    public static StreamExecutionEnvironment getConfigStreamExecutionEnvironment(ParameterTool parameter) {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(parameter);
        return env;
    }

    public static StreamExecutionEnvironment getSingleParallelismStreamExecutionEnvironment() {
        Configuration config = new Configuration();
        config.set(BIND_PORT, "28899");
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.setParallelism(1);
        return env;
    }
}
