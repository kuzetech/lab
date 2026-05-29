package com.kuzetech.bigdata.lab.flink20.sql.base.production;

import com.kuzetech.bigdata.lab.flink20.sql.core.util.StreamExecutionEnvironmentUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class EventCount {
    public static void main(String[] args) {

        /*
        --
        --parallelism.default 1
        * */

        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironmentUtil.getConfigStreamExecutionEnvironment(parameterTool);
        //streamExecutionEnvironment.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(streamExecutionEnvironment);

        /*
         * {"#event_time":1779955200000,"#user_id":"user-fake8697","level":15}      2026-05-28 16:00:00
         * {"#event_time":1779955800000,"#user_id":"user-fake8697","level":15}      2026-05-28 16:10:00
         * */

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE source (
                    `#device_id` STRING,
                    `#user_id` STRING,
                    `#event_time` BIGINT,
                    event AS IF(`#device_id` IS NOT NULL, 'DEVICE_MUTATION', 'USER_MUTATION'),
                    ts AS CAST(TO_TIMESTAMP_LTZ(`#event_time`, 3) AS TIMESTAMP(3)),
                    WATERMARK FOR ts AS ts - INTERVAL '5' MINUTES
                ) WITH (
                    'scan.watermark.emit.strategy'='on-event',
                    'scan.watermark.idle-timeout'='10s',
                    'connector' = 'kafka',
                    'topic' = 'test-production',
                    'properties.bootstrap.servers' = 'localhost:9092',
                    'properties.group.id' = 'testProductionGroup',
                    'scan.startup.mode' = 'group-offsets',
                    'properties.auto.offset.reset' = 'earliest',
                    'value.format' = 'json',
                    'value.json.ignore-parse-errors' = 'true'
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
                FROM TABLE(TUMBLE(TABLE source, DESCRIPTOR(ts), INTERVAL '5' MINUTES))
                GROUP BY window_start, window_end, event
                """);
    }
}
