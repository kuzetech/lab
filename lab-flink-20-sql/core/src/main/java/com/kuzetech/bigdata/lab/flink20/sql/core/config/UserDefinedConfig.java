package com.kuzetech.bigdata.lab.flink20.sql.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.java.utils.ParameterTool;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDefinedConfig {
    private String appFilter;

    public static UserDefinedConfig getInstance(ParameterTool tool) {
        return new UserDefinedConfig(
                tool.get("user.defined.filter.app", "test")
        );
    }
}
