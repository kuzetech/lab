package com.kuzetech.bigdata.lab.flink20.sql.base;

import com.kuzetech.bigdata.lab.flink20.sql.core.util.EnvironmentSettingsUtil;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

public class Gen2Print {
    public static void main(String[] args) {
        EnvironmentSettings settings = EnvironmentSettingsUtil.getEnvironmentSettings();

        TableEnvironment tableEnv = TableEnvironment.create(settings);

        // Using SQL DDL
        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE source (
                    order_number BIGINT,
                    price        DECIMAL(32,2),
                    buyer        ROW<first_name STRING, last_name STRING>,
                    order_time   TIMESTAMP(3)
                ) WITH (
                    'connector' = 'datagen'
                )
                """);


        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE sink (
                    order_number BIGINT,
                    price        DECIMAL(32,2),
                    buyer        ROW<first_name STRING, last_name STRING>,
                    order_time   TIMESTAMP(3)
                ) WITH (
                    'connector' = 'print'
                )
                """);

        tableEnv.executeSql("""
                INSERT INTO sink
                SELECT
                    *
                FROM source
                """);
    }
}
