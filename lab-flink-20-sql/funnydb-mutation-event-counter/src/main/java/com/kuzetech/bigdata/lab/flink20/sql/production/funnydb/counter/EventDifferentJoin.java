package com.kuzetech.bigdata.lab.flink20.sql.production.funnydb.counter;

import com.kuzetech.bigdata.lab.flink20.sql.core.config.JobConfig;
import com.kuzetech.bigdata.lab.flink20.sql.core.util.StreamExecutionEnvironmentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

@Slf4j
public class EventDifferentJoin {
    public static void main(String[] args) {

        /*  idea 运行参数
            --job.parallelism 1
            --connector.kafka.topic wu_la_la_quan_qiu_fu_wv18n35j-flink-users
            --connector.kafka.group.id testProductionMutationDifferentGroup
            --user.defined.filter.app test
        */

        /*  kafka 写入数据
            wu_la_la_quan_qiu_fu_wv18n35j-flink-users
            {"#event_time":1779955200000,"#user_id":"1","level":15}      2026-05-28 16:00:00
            {"#event_time":1779955200000,"#user_id":"2","level":15}      2026-05-28 16:00:00
            {"#event_time":1779955800000,"#user_id":"1","level":15}      2026-05-28 16:10:00

            funnydb-mutation-test-output
            header{"app":"test","mutation_type":"USER"} {"#event_time":1779955200000,"#user_id":"1","level":15}      2026-05-28 16:00:00
        */

        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        JobConfig jobConfig = JobConfig.getInstance(parameterTool);

        log.info("jobConfig: {}", jobConfig);

        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironmentUtil.getConfigStreamExecutionEnvironment(parameterTool);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(streamExecutionEnvironment);

        String sourceMutationSql =
                "CREATE TEMPORARY TABLE source_mutation (                                                   " +
                        "   `#user_id` STRING,                                                              " +
                        "   `#event_time` BIGINT,                                                           " +
                        "   `partition` BIGINT METADATA VIRTUAL,                                            " +
                        "   `offset` BIGINT METADATA VIRTUAL,                                               " +
                        "   headers MAP<STRING, BYTES> METADATA FROM 'headers' VIRTUAL,                     " +
                        "   ts AS CAST(TO_TIMESTAMP_LTZ(`#event_time`, 3) AS TIMESTAMP(3)),                 " +
                        "   WATERMARK FOR ts AS ts - INTERVAL '5' MINUTES                                   " +
                        ") WITH (                                                                           " +
                        "   'scan.watermark.emit.strategy'='on-event',                                      " +
                        "   'scan.watermark.idle-timeout'='1m',                                            " +
                        "   'scan.topic-partition-discovery.interval'='5m',                                 " +
                        "   'connector' = 'kafka',                                                          " +
                        "   'topic' = 'funnydb-mutation-test-output',                                       " +
                        "   'properties.bootstrap.servers' = '%s',                                          " +
                        "   'properties.isolation.level' = 'read_committed',                                " +
                        "   'properties.group.id' = '%s',                                                   " +
                        "   'scan.startup.mode' = 'group-offsets',                                          " +
                        "   'properties.auto.offset.reset' = '%s',                                          " +
                        "   'value.format' = 'json',                                                        " +
                        "   'value.json.ignore-parse-errors' = 'true'                                       " +
                        ")                                                                                  ";

        tableEnv.executeSql(String.format(
                sourceMutationSql,
                jobConfig.getKafkaConfig().getBootstrapServers(),
                jobConfig.getKafkaConfig().getGroupId(),
                jobConfig.getKafkaConfig().getOffsetReset()));

        String sourceFlinkSql =
                "CREATE TEMPORARY TABLE source_flink (                                                      " +
                        "   `#user_id` STRING,                                                              " +
                        "   `#event_time` BIGINT,                                                           " +
                        "   `partition` BIGINT METADATA VIRTUAL,                                            " +
                        "   `offset` BIGINT METADATA VIRTUAL,                                               " +
                        "   ts AS CAST(TO_TIMESTAMP_LTZ(`#event_time`, 3) AS TIMESTAMP(3)),                 " +
                        "   WATERMARK FOR ts AS ts - INTERVAL '5' MINUTES                                   " +
                        ") WITH (                                                                           " +
                        "   'scan.watermark.emit.strategy'='on-event',                                      " +
                        "   'scan.watermark.idle-timeout'='1m',                                            " +
                        "   'scan.topic-partition-discovery.interval'='5m',                                 " +
                        "   'connector' = 'kafka',                                                          " +
                        "   'topic' = '%s',                                                                 " +
                        "   'properties.bootstrap.servers' = '%s',                                          " +
                        "   'properties.isolation.level' = 'read_committed',                                " +
                        "   'properties.group.id' = '%s',                                                   " +
                        "   'scan.startup.mode' = 'group-offsets',                                          " +
                        "   'properties.auto.offset.reset' = '%s',                                          " +
                        "   'value.format' = 'json',                                                        " +
                        "   'value.json.ignore-parse-errors' = 'true'                                       " +
                        ")                                                                                  ";

        tableEnv.executeSql(String.format(sourceFlinkSql,
                jobConfig.getKafkaConfig().getTopic(),
                jobConfig.getKafkaConfig().getBootstrapServers(),
                jobConfig.getKafkaConfig().getGroupId(),
                jobConfig.getKafkaConfig().getOffsetReset()));

        String sinkSql =
                "CREATE TEMPORARY TABLE sink (                                                       " +
                        "    `#user_id` STRING,                                                      " +
                        "    `#event_time` BIGINT,                                                   " +
                        "    `partition` BIGINT,                                                     " +
                        "    `offset` BIGINT                                                         " +
                        ") WITH (                                                                    " +
                        "    'connector' = 'print'                                                   " +
                        ")                                                                           ";
        ;

        tableEnv.executeSql(sinkSql);

        String execSql =
                "INSERT INTO sink                                                     " +
                        "SELECT                                                       " +
                        "  a.`#user_id`,                                              " +
                        "  a.`#event_time`,                                           " +
                        "  a.`partition`,                                             " +
                        "  a.`offset`                                                 " +
                        "FROM source_flink AS a                                       " +
                        "LEFT JOIN(                                                   " +
                        "  SELECT                                                     " +
                        "    `#user_id`,                                              " +
                        "    `#event_time`,                                           " +
                        "    ts                                                       " +
                        "  FROM source_mutation                                       " +
                        "  WHERE CAST(headers['app'] AS STRING) = '%s'                " +
                        "  AND CAST(headers['mutation_type'] AS STRING) = 'USER'      " +
                        ") AS b                                                       " +
                        "ON a.`#user_id` = b.`#user_id`                               " +
                        "AND a.`#event_time` = b.`#event_time`                        " +
                        "AND a.ts BETWEEN b.ts - INTERVAL '3' MINUTE                  " +
                        "             AND b.ts + INTERVAL '3' MINUTE                  " +
                        "WHERE b.`#user_id` IS NULL                                   ";

        tableEnv.executeSql(String.format(execSql, jobConfig.getUserDefinedConfig().getAppFilter()));
    }
}
