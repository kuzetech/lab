package com.kuzetech.bigdata.lab.flink20.sql.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.java.utils.ParameterTool;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JdbcConfig {
    private String url;
    private String username;
    private String password;
    private String table;

    public static JdbcConfig getInstance(ParameterTool tool) {
        return new JdbcConfig(
                tool.get("connector.jdbc.url", "jdbc:postgresql://localhost:5432/app"),
                tool.get("connector.jdbc.username", "app"),
                tool.get("connector.jdbc.password", "123456"),
                tool.get("connector.jdbc.table", "record")
        );
    }
}
