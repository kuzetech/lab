package com.kuzetech.bigdata.flink.util;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.BatchExecutionOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkEnvironmentUtil {
    public static StreamExecutionEnvironment getDefaultStreamExecutionEnvironment() {
        Configuration config = new Configuration();
        config.set(BatchExecutionOptions.ADAPTIVE_AUTO_PARALLELISM_ENABLED, false);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        return env;
    }

}
