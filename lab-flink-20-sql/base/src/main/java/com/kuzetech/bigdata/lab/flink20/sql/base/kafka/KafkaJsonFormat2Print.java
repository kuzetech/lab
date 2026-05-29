package com.kuzetech.bigdata.lab.flink20.sql.base.kafka;

import com.kuzetech.bigdata.lab.flink20.sql.core.util.EnvironmentSettingsUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.ZoneId;

public class KafkaJsonFormat2Print {
    public static void main(String[] args) {
        StreamExecutionEnvironment streamExecutionEnvironment = EnvironmentSettingsUtil.getSingleParallelismStreamExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(streamExecutionEnvironment);
        tableEnv.getConfig().setLocalTimeZone(ZoneId.of("Asia/Shanghai"));

        // {"level":15,"price":15.55,"#event_time":1779955200000,"#user_id":"user-fake8697"}
        // event_time = 2026-05-28 16:00:00

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE source (
                    level           INT,
                    price           DECIMAL(32,2),
                    `#event_time`   BIGINT,
                    ts AS CAST(TO_TIMESTAMP_LTZ(`#event_time`, 3) AS TIMESTAMP(3)),
                    `#user_id`      STRING
                ) WITH (
                    'connector' = 'kafka',
                    'topic' = 'test-json',
                    'properties.bootstrap.servers' = 'localhost:9092',
                    'properties.group.id' = 'testJsonGroup',
                    'scan.startup.mode' = 'group-offsets',
                    'properties.auto.offset.reset' = 'earliest',
                    'value.format' = 'json',
                    'value.json.ignore-parse-errors' = 'true'
                )
                """);

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE sink (
                    level           INT,
                    price           DECIMAL(32,2),
                    `#event_time`   BIGINT,
                    ts              TIMESTAMP(3),
                    `#user_id`      STRING
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
