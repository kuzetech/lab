package com.kuzetech.bigdata.lab.flink20.sql.base.kafka;

import com.kuzetech.bigdata.lab.flink20.sql.core.util.EnvironmentSettingsUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

public class Kafka2Kafka {
    public static void main(String[] args) {
        ParameterTool parameter = ParameterTool.fromArgs(args);
        String kafkaBootstrapServers = parameter.get("kafka.bootstrap.servers", "localhost:9092");

        EnvironmentSettings settings = EnvironmentSettingsUtil.getCheckPointEnvironmentSettings();

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
                    'properties.bootstrap.servers' = '%s',
                    'properties.group.id' = 'testGroup',
                    'scan.startup.mode' = 'group-offsets',
                    'properties.auto.offset.reset' = 'earliest',
                    'value.format' = 'json',
                    'value.json.ignore-parse-errors' = 'true'
                )
                """
                .formatted(kafkaBootstrapServers)
        );

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE sink (
                    order_number BIGINT,
                    price        DECIMAL(32,2),
                    buyer        ROW<first_name STRING, last_name STRING>,
                    order_time   TIMESTAMP(3),
                    `offset`     BIGINT
                ) WITH (
                    'connector' = 'kafka',
                    'topic' = 'output',
                    'properties.bootstrap.servers' = '%s',
                    'sink.delivery-guarantee' = 'exactly-once',
                    'sink.transactional-id-prefix' = 'my-prefix',
                    'properties.transaction.timeout.ms' = '900000',
                    'properties.isolation.level' = 'read_committed',
                    'properties.compression.type' = 'lz4',
                    'properties.batch.size' = '32768',
                    'properties.linger.ms' = '5',
                    'key.format' = 'json',
                    'key.json.ignore-parse-errors' = 'true',
                    'key.fields' = 'order_number',
                    'value.format' = 'json',
                    'value.json.ignore-parse-errors' = 'true',
                    'value.json.fail-on-missing-field' = 'false',
                    'value.fields-include' = 'ALL'
                )
                """
                .formatted(kafkaBootstrapServers)
        );

        tableEnv.executeSql("""
                INSERT INTO sink
                SELECT
                    *
                FROM source
                """);
    }
}
