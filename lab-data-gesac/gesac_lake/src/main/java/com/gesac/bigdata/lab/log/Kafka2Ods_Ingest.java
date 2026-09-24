package com.gesac.bigdata.lab.log;

import com.gesac.bigdata.lab.common.EnvironmentUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class Kafka2Ods_Ingest {
    public static void main(String[] args) {
        StreamExecutionEnvironment env =
                EnvironmentUtil.generateStreamExecutionEnvironment("log_source_to_ods");
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        tableEnv.executeSql("""
                CREATE CATALOG hive_catalog WITH (
                    'type' = 'paimon',
                    'metastore' = 'hive',
                    'uri' = 'thrift://localhost:9083',
                    'hive-conf-dir' = 'src/main/resources',
                    'warehouse' = 'hdfs://namenode:9000/paimon/hive'
                );
                """);

        tableEnv.executeSql("""
                CREATE DATABASE IF NOT EXISTS hive_catalog.gesac_lake;
                """);

        tableEnv.executeSql("""
                USE hive_catalog.gesac_lake;
                """);

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE kafka_log (
                    `event`   STRING,
                    `page`    STRING,
                    `ts`      BIGINT
                ) WITH (
                    'connector' = 'kafka',
                    'topic' = 'source_log',
                    'properties.bootstrap.servers' = 'localhost:9092',
                    'properties.group.id' = 'log_source_to_ods',
                    'scan.startup.mode' = 'earliest-offset',
                    'format' = 'json'
                )
                """);

        tableEnv.executeSql("""
                CREATE TABLE IF NOT EXISTS ods_log (
                    `event`     string,
                    `page`      string,
                    `ts`        bigint,
                    `dt`        varchar(10)
                ) PARTITIONED BY (dt) WITH (
                    'bucket-key' = 'event',
                    'bucket' = '2',
                    'path' = 'hdfs://namenode:9000/paimon/hive/gesac_lake.db/ods_log',
                    'sink.parallelism' = '2',
                    'metastore.partitioned-table'='true'
                );
                """);


        tableEnv.executeSql("""
                INSERT INTO ods_log
                SELECT
                    `event`,
                    `page`,
                    `ts`,
                    DATE_FORMAT(TO_TIMESTAMP_LTZ(`ts`, 3), 'yyyy-MM-dd')
                FROM kafka_log
                """);


    }
}
