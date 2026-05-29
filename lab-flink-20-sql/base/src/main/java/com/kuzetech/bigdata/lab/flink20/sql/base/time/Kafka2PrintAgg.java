package com.kuzetech.bigdata.lab.flink20.sql.base.time;

import com.kuzetech.bigdata.lab.flink20.sql.core.util.StreamExecutionEnvironmentUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.ZoneId;

public class Kafka2PrintAgg {
    public static void main(String[] args) {

        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironmentUtil.getSingleParallelismStreamExecutionEnvironment();

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
                    'topic' = 'test-time',
                    'properties.bootstrap.servers' = 'localhost:9092',
                    'properties.group.id' = 'testTimeGroup',
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
                    total   BIGINT
                ) WITH (
                    'connector' = 'print'
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
