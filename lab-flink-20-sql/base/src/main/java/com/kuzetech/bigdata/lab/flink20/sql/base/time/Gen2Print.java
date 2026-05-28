package com.kuzetech.bigdata.lab.flink20.sql.base.time;

import com.kuzetech.bigdata.lab.flink20.sql.core.util.EnvironmentSettingsUtil;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

public class Gen2Print {
    public static void main(String[] args) {
        EnvironmentSettings settings = EnvironmentSettingsUtil.getEnvironmentSettings();

        TableEnvironment tableEnv = TableEnvironment.create(settings);

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE source (
                    id INT,
                    event_time TIMESTAMP,
                    proc_time AS PROCTIME(),
                    WATERMARK FOR event_time AS event_time - INTERVAL '60' SECOND
                ) WITH (
                    'connector' = 'datagen',
                    'scan.parallelism' = '1',
                    'fields.id.kind' = 'random',
                    'fields.id.min' = '1',
                    'fields.id.max' = '10',
                    'fields.ts.max-past' = '60000',
                    'rows-per-second' = '1',
                    'scan.watermark.emit.strategy'='on-event'
                )
                """);


        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE sink (
                    id  INT,
                    event_time TIMESTAMP,
                    proc_time TIMESTAMP
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
