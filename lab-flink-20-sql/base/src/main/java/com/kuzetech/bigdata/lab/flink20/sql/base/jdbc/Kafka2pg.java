package com.kuzetech.bigdata.lab.flink20.sql.base.jdbc;

import com.kuzetech.bigdata.lab.flink20.sql.core.util.EnvironmentSettingsUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.ZoneId;

public class Kafka2pg {
    public static void main(String[] args) {

        StreamExecutionEnvironment streamExecutionEnvironment = EnvironmentSettingsUtil.getSingleParallelismStreamExecutionEnvironment();

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(streamExecutionEnvironment);
        tableEnv.getConfig().setLocalTimeZone(ZoneId.of("Asia/Shanghai"));

        // login,2026-05-28 10:00:00.000

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE source (
                    event STRING,
                    event_time TIMESTAMP(3),
                    proc_time AS PROCTIME(),
                    WATERMARK FOR event_time AS event_time - INTERVAL '60' SECOND
                ) WITH (
                    'connector' = 'kafka',
                    'topic' = 'test-pg',
                    'properties.bootstrap.servers' = 'localhost:9092',
                    'properties.group.id' = 'testPgGroup',
                    'scan.startup.mode' = 'group-offsets',
                    'properties.auto.offset.reset' = 'earliest',
                    'value.format' = 'csv',
                    'value.csv.ignore-parse-errors' = 'true'
                )
                """);


        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE sink (
                    window_start    TIMESTAMP(3),
                    window_end  TIMESTAMP(3),
                    event STRING,
                    total   BIGINT,
                    PRIMARY KEY (window_start, window_end, event) NOT ENFORCED
                ) WITH (
                    'connector' = 'jdbc',
                    'url' = 'jdbc:postgresql://localhost:5432/app',
                    'table-name' = 'record',
                    'username' = 'app',
                    'password' = '123456'
                )
                """);

        tableEnv.executeSql("""
                INSERT INTO sink
                SELECT
                    window_start,
                    window_end,
                    event,
                    count(1) AS total
                FROM TABLE(TUMBLE(TABLE source, DESCRIPTOR(event_time), INTERVAL '5' MINUTES))
                GROUP BY window_start, window_end, event;
                """);
    }
}
