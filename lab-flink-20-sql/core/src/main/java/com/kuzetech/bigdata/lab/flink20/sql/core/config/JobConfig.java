package com.kuzetech.bigdata.lab.flink20.sql.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.java.utils.ParameterTool;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobConfig {
    private KafkaConfig kafkaConfig;
    private JdbcConfig jdbcConfig;
    private UserDefinedConfig userDefinedConfig;

    public static JobConfig getInstance(ParameterTool tool) {
        return new JobConfig(
                KafkaConfig.getInstance(tool),
                JdbcConfig.getInstance(tool),
                UserDefinedConfig.getInstance(tool)
        );
    }
}
