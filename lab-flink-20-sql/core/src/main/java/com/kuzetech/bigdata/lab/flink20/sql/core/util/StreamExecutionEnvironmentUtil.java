package com.kuzetech.bigdata.lab.flink20.sql.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

@Slf4j
public class StreamExecutionEnvironmentUtil {
    public static StreamExecutionEnvironment getConfigStreamExecutionEnvironment(ParameterTool parameter) {
        Configuration config = new Configuration();
        config.addAll(ConfigurationUtil.generateCheckpointConfiguration(parameter));
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        if (parameter.has("job.parallelism")) {
            int udParallelism = parameter.getInt("job.parallelism");
            log.info("user defined job.parallelism : {}", udParallelism);
            env.setParallelism(udParallelism);
        }
        return env;
    }

    public static StreamExecutionEnvironment getSingleParallelismStreamExecutionEnvironment() {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        return env;
    }
}
