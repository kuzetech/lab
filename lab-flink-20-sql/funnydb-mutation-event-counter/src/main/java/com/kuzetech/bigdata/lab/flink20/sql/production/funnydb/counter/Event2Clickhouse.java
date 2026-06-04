package com.kuzetech.bigdata.lab.flink20.sql.production.funnydb.counter;

import com.kuzetech.bigdata.lab.flink20.sql.core.config.JobConfig;
import com.kuzetech.bigdata.lab.flink20.sql.core.util.StreamExecutionEnvironmentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.ZoneId;

@Slf4j
public class Event2Clickhouse {
    public static void main(String[] args) {
        /*
         idea 运行参数
         --job.parallelism 1
         --job.checkpoint.enable true
         --connector.kafka.topic wu_la_la_quan_qiu_fu_wv18n35j-flink-users
         --connector.kafka.group.id event2ClickhouseGroup
         --connector.kafka.bootstrap.servers localhost:9092
         --connector.kafka.offset.reset earliest
         --connector.jdbc.url jdbc:clickhouse://localhost:8123/app
         --connector.jdbc.username app
         --connector.jdbc.password 123456
         --connector.jdbc.table app.record
         */

        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        JobConfig jobConfig = JobConfig.getInstance(parameterTool);

        log.info("jobConfig: {}", jobConfig);

        StreamExecutionEnvironment streamExecutionEnvironment =
                StreamExecutionEnvironmentUtil.getConfigStreamExecutionEnvironment(parameterTool);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(streamExecutionEnvironment);
        tableEnv.getConfig().setLocalTimeZone(ZoneId.of("Asia/Shanghai"));

        String sourceSql =
                "CREATE TEMPORARY TABLE source (                                                        " +
                        "   kafka_topic STRING METADATA FROM 'topic' VIRTUAL,                             " +
                        "   kafka_partition BIGINT METADATA FROM 'partition' VIRTUAL,                     " +
                        "   kafka_offset BIGINT METADATA FROM 'offset' VIRTUAL,                           " +
                        "   headers MAP<STRING, BYTES> METADATA FROM 'headers' VIRTUAL,                   " +
                        "   payload STRING                                                                " +
                        ") WITH (                                                                         " +
                        "   'connector' = 'kafka',                                                        " +
                        "   'topic' = '%s',                                                               " +
                        "   'properties.bootstrap.servers' = '%s',                                        " +
                        "   'properties.group.id' = '%s',                                                 " +
                        "   'properties.isolation.level' = 'read_committed',                              " +
                        "   'scan.startup.mode' = 'group-offsets',                                        " +
                        "   'properties.auto.offset.reset' = '%s',                                        " +
                        "   'scan.topic-partition-discovery.interval' = '5m',                             " +
                        "   'value.format' = 'raw'                                                        " +
                        ")";

        tableEnv.executeSql(String.format(
                sourceSql,
                jobConfig.getKafkaConfig().getTopic(),
                jobConfig.getKafkaConfig().getBootstrapServers(),
                jobConfig.getKafkaConfig().getGroupId(),
                jobConfig.getKafkaConfig().getOffsetReset()));

        String sinkSql =
                "CREATE TEMPORARY TABLE sink (                                                          " +
                        "    kafka_topic STRING,                                                         " +
                        "    kafka_partition INT,                                                        " +
                        "    kafka_offset BIGINT,                                                        " +
                        "    user_id STRING,                                                             " +
                        "    event_time BIGINT,                                                          " +
                        "    payload STRING,                                                             " +
                        "    ingested_at TIMESTAMP(3)                                                    " +
                        ") WITH (                                                                         " +
                        "    'connector' = 'jdbc',                                                        " +
                        "    'url' = '%s',                                                                " +
                        "    'table-name' = '%s',                                                         " +
                        "    'username' = '%s',                                                           " +
                        "    'password' = '%s',                                                           " +
                        "    'sink.buffer-flush.max-rows' = '200',                                        " +
                        "    'sink.buffer-flush.interval' = '1s',                                         " +
                        "    'sink.max-retries' = '3'                                                     " +
                        ")";

        tableEnv.executeSql(String.format(
                sinkSql,
                jobConfig.getJdbcConfig().getUrl(),
                jobConfig.getJdbcConfig().getTable(),
                jobConfig.getJdbcConfig().getUsername(),
                jobConfig.getJdbcConfig().getPassword()));

        String insertSql =
                "INSERT INTO sink                                                                    " +
                        "SELECT                                                                           " +
                        "    kafka_topic,                                                                 " +
                        "    CAST(kafka_partition AS INT),                                                " +
                        "    kafka_offset,                                                                " +
                        "    REGEXP_EXTRACT(payload, '#user_id\":\"([^\"]+)\"', 1) AS user_id,           " +
                        "    CAST(REGEXP_EXTRACT(payload, '#event_time\":([0-9]+)', 1) AS BIGINT) AS event_time, " +
                        "    payload,                                                                     " +
                        "    CURRENT_TIMESTAMP AS ingested_at                                             " +
                        "FROM source                                                                      " +
                        "WHERE (                                                                          " +
                        "       headers['app'] IS NULL                                                    " +
                        "       OR CAST(headers['app'] AS STRING) = '%s'                                  " +
                        "  )                                                                              " +
                        "  AND (                                                                          " +
                        "       headers['mutation_type'] IS NULL                                           " +
                        "       OR CAST(headers['mutation_type'] AS STRING) = 'USER'                      " +
                        "  )                                                                              ";

        tableEnv.executeSql(String.format(insertSql, jobConfig.getUserDefinedConfig().getAppFilter()));
    }
}
