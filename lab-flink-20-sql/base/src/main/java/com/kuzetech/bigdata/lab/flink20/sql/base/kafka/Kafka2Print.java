package com.kuzetech.bigdata.lab.flink20.sql.base.kafka;

import com.kuzetech.bigdata.lab.flink20.sql.core.util.EnvironmentSettingsUtil;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

public class Kafka2Print {
    public static void main(String[] args) {
        EnvironmentSettings settings = EnvironmentSettingsUtil.getEnvironmentSettings();

        TableEnvironment tableEnv = TableEnvironment.create(settings);

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE source (
                    order_number BIGINT,
                    price        DECIMAL(32,2),
                    buyer        ROW<first_name STRING, last_name STRING>,
                    order_time   TIMESTAMP(3),
                    `offset`     BIGINT METADATA FROM 'offset'
                ) WITH (
                    'connector' = 'kafka',
                    'topic' = 'test',
                    'properties.bootstrap.servers' = 'localhost:9092',
                    'format' = 'json',
                    'properties.group.id' = 'testGroup',
                    'scan.startup.mode' = 'latest-offset',
                    'json.ignore-parse-errors' = 'true'
                )
                """);

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE sink (
                    order_number BIGINT,
                    price        DECIMAL(32,2),
                    buyer        ROW<first_name STRING, last_name STRING>,
                    order_time   TIMESTAMP(3),
                    `offset`     BIGINT
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
