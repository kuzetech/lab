package com.kuzetech.bigdata.lab.flink20.sql.base.time;

import com.kuzetech.bigdata.lab.flink20.sql.core.util.EnvironmentSettingsUtil;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

import java.time.ZoneId;

public class File2Print {
    public static void main(String[] args) {
        EnvironmentSettings settings = EnvironmentSettingsUtil.getEnvironmentSettings();

        TableEnvironment tableEnv = TableEnvironment.create(settings);
        tableEnv.getConfig().setLocalTimeZone(ZoneId.of("Asia/Shanghai"));

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE source (
                    event STRING,
                    event_time TIMESTAMP(3),
                    proc_time AS PROCTIME(),
                    WATERMARK FOR event_time AS event_time - INTERVAL '60' SECOND
                ) WITH (
                    'connector' = 'filesystem',
                    'path' = './data/time/events.csv',
                    'format' = 'csv',
                    'csv.allow-comments' = 'true',
                    'csv.field-delimiter' = ',',
                    'csv.ignore-parse-errors' = 'false',
                    'source.monitor-interval' = '10s'
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
                FROM TABLE(TUMBLE(TABLE source, DESCRIPTOR(event_time), INTERVAL '1' MINUTES))
                GROUP BY window_start, window_end, event;
                """);
    }
}
