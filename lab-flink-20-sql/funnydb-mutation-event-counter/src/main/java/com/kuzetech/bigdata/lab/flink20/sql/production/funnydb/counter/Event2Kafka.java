package com.kuzetech.bigdata.lab.flink20.sql.production.funnydb.counter;

import com.kuzetech.bigdata.lab.flink20.sql.core.config.JobConfig;
import com.kuzetech.bigdata.lab.flink20.sql.core.util.StreamExecutionEnvironmentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

@Slf4j
public class Event2Kafka {
    public static void main(String[] args) {

        /*  idea 运行参数
            --job.parallelism 1
            --connector.kafka.topic demo;output
            --connector.kafka.group.id Event2Kafka
            --user.defined.filter.app demo
        */

        /*  kafka 写入数据
            demo
            {"#event_time":1779955200000,"#user_id":"demo","level":15}

            output
            header{"app":"test","mutation_type":"USER"} {"#event_time":1779955200000,"#user_id":"test","level":15}
            header{"app":"demo","mutation_type":"USER"} {"#event_time":1779955200000,"#user_id":"demo","level":15}
        */

        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        JobConfig jobConfig = JobConfig.getInstance(parameterTool);

        log.info("jobConfig: {}", jobConfig);

        StreamExecutionEnvironment streamExecutionEnvironment = StreamExecutionEnvironmentUtil.getConfigStreamExecutionEnvironment(parameterTool);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(streamExecutionEnvironment);

        String sourceSql =
                "CREATE TEMPORARY TABLE source (                                                            " +
                        "   `#user_id` STRING,                                                              " +
                        "   `#event_time` BIGINT,                                                           " +
                        "   `topic` STRING METADATA VIRTUAL,                                            " +
                        "   `partition` INT METADATA VIRTUAL,                                            " +
                        "   `offset` BIGINT METADATA VIRTUAL,                                               " +
                        "   headers MAP<STRING, BYTES> METADATA FROM 'headers' VIRTUAL                      " +
                        ") WITH (                                                                           " +
                        "   'scan.topic-partition-discovery.interval'='5m',                                 " +
                        "   'connector' = 'kafka',                                                          " +
                        "   'topic' = '%s',                                                                 " +
                        "   'properties.bootstrap.servers' = '%s',                                          " +
                        "   'properties.isolation.level' = 'read_committed',                                " +
                        "   'properties.group.id' = '%s',                                                   " +
                        "   'scan.startup.mode' = 'group-offsets',                                          " +
                        "   'properties.auto.offset.reset' = '%s',                                          " +
                        "   'value.format' = 'json',                                                          " +
                        "   'value.json.ignore-parse-errors' = 'true'                                                          " +
                        ")                                                                                  ";

        tableEnv.executeSql(String.format(
                sourceSql,
                jobConfig.getKafkaConfig().getTopic(),
                jobConfig.getKafkaConfig().getBootstrapServers(),
                jobConfig.getKafkaConfig().getGroupId(),
                jobConfig.getKafkaConfig().getOffsetReset()));

        String sinkSql =
                "CREATE TEMPORARY TABLE sink (                                                       " +
                        "   `#user_id` STRING,                                                              " +
                        "   `#event_time` BIGINT,                                                           " +
                        "   `payload` STRING,                                                           " +
                        "   `topic` STRING,                                            " +
                        "   `partition` INT,                                            " +
                        "   `offset` BIGINT                                               " +
                        ") WITH (                                                                    " +
                        "    'connector' = 'kafka',                                                  " +
                        "    'topic' = 'mutation-validator',                                         " +
                        "    'sink.delivery-guarantee' = 'exactly-once',                             " +
                        "    'sink.transactional-id-prefix' = '%s',                  " +
                        "    'properties.bootstrap.servers' = '%s',                                  " +
                        "    'properties.transaction.timeout.ms' = '900000',                         " +
                        "    'properties.compression.type' = 'lz4',                                  " +
                        "    'properties.batch.size' = '32768',                                      " +
                        "    'properties.linger.ms' = '5',                                           " +
                        "    'key.format' = 'json',                                                  " +
                        "    'key.json.ignore-parse-errors' = 'true',                                " +
                        "    'key.fields' = '#user_id',                                               " +
                        "    'value.format' = 'json',                                                " +
                        "    'value.json.ignore-parse-errors' = 'true',                              " +
                        "    'value.json.fail-on-missing-field' = 'false',                           " +
                        "    'value.fields-include' = 'ALL'                                          " +
                        ")                                                                           ";

        tableEnv.executeSql(String.format(
                sinkSql,
                jobConfig.getKafkaConfig().getBootstrapServers(),
                jobConfig.getKafkaConfig().getTransactionPrefix()
        ));

        String execSql =
                "INSERT INTO sink                                                                         " +
                        "SELECT                                                                           " +
                        "   `#user_id`,                                                              " +
                        "   `#event_time`,                                                           " +
                        "   '',                                                           " +
                        "   `topic`,                                            " +
                        "   `partition`,                                            " +
                        "   `offset`                                              " +
                        "FROM source                                                                      " +
                        "WHERE (                                                                          " +
                        "       headers['app'] IS NULL                                                    " +
                        "       OR CAST(headers['app'] AS STRING) = '%s'                                  " +
                        "  )                                                                              " +
                        "  AND (                                                                          " +
                        "       headers['mutation_type'] IS NULL                                          " +
                        "       OR CAST(headers['mutation_type'] AS STRING) = 'USER'                      " +
                        "  )                                                                              ";

        tableEnv.executeSql(String.format(
                execSql,
                jobConfig.getUserDefinedConfig().getAppFilter()
        ));
    }
}
