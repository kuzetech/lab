package com.kuzetech.bigdata.lab.flink20.sql.base.kafka;

import com.kuzetech.bigdata.lab.flink20.sql.core.util.EnvironmentSettingsUtil;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

public class Gen2Kafka {
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
                    'connector' = 'datagen',
                    'number-of-rows' = '10',
                    'rows-per-second' = '1'
                )
                """);


        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE sink (
                    order_number BIGINT,
                    price        DECIMAL(32,2),
                    buyer        ROW<first_name STRING, last_name STRING>,
                    order_time   TIMESTAMP(3)
                ) WITH (
                    'connector' = 'kafka',
                    'topic' = 'test',
                    'properties.bootstrap.servers' = 'localhost:9092',
                    'key.format' = 'json',
                    'key.json.ignore-parse-errors' = 'true',
                    'key.fields' = 'order_number',
                    'value.format' = 'json',
                    'value.json.ignore-parse-errors' = 'true',
                    'value.json.fail-on-missing-field' = 'false',
                    'value.fields-include' = 'ALL'
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
