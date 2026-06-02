package com.kuzetech.bigdata.lab.flink20.sql.production.funnydb.counter;

import com.kuzetech.bigdata.lab.flink20.sql.core.config.JobConfig;
import com.kuzetech.bigdata.lab.flink20.sql.core.util.StreamExecutionEnvironmentUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

@Slf4j
public class FlinkEventCount {
    public static void main(String[] args) {

        /*  idea 运行参数
            --job.parallelism 1
            --connector.kafka.topic test-production-flink
            --connector.kafka.group.id testProductionFlinkGroup
            --connector.jdbc.table counter
        */

        /*  kafka 写入数据
            {"#event_time":1779955200000,"#user_id":"user-fake8697","level":15}      2026-05-28 16:00:00
            {"#event_time":1779955800000,"#user_id":"user-fake8697","level":15}      2026-05-28 16:10:00
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
                        "   ts AS CAST(TO_TIMESTAMP_LTZ(`#event_time`, 3) AS TIMESTAMP(3)),                 " +
                        "   WATERMARK FOR ts AS ts - INTERVAL '5' MINUTES                                   " +
                        ") WITH (                                                                           " +
                        "   'scan.watermark.emit.strategy'='on-event',                                      " +
                        "   'scan.watermark.idle-timeout'='15s',                                            " +
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

        tableEnv.executeSql(String.format(sourceSql,
                jobConfig.getKafkaConfig().getTopic(),
                jobConfig.getKafkaConfig().getBootstrapServers(),
                jobConfig.getKafkaConfig().getGroupId(),
                jobConfig.getKafkaConfig().getOffsetReset()));

        String sinkSql =
                "CREATE TEMPORARY TABLE sink (                                                          " +
                        "    app STRING,                                                                " +
                        "    window_start TIMESTAMP(3),                                                 " +
                        "    window_end  TIMESTAMP(3),                                                  " +
                        "    event STRING,                                                              " +
                        "    total BIGINT,                                                              " +
                        "    PRIMARY KEY (app, window_start, window_end, event) NOT ENFORCED            " +
                        ") WITH (                                                                       " +
                        "    'connector' = 'jdbc',                                                      " +
                        "    'url' = '%s',                                                              " +
                        "    'table-name' = '%s',                                                       " +
                        "    'username' = '%s',                                                         " +
                        "    'password' = '%s'                                                          " +
                        ")                                                                              ";
        ;

        tableEnv.executeSql(String.format(sinkSql,
                jobConfig.getJdbcConfig().getUrl(),
                jobConfig.getJdbcConfig().getTable(),
                jobConfig.getJdbcConfig().getUsername(),
                jobConfig.getJdbcConfig().getPassword()));

        tableEnv.executeSql(
                "INSERT INTO sink                                                                        " +
                        "SELECT                                                                          " +
                        "    'flink' AS app,                                                             " +
                        "    window_start,                                                               " +
                        "    window_end,                                                                 " +
                        "    'USER' AS event,                                                            " +
                        "    count(1) AS total                                                           " +
                        "FROM TABLE(TUMBLE(TABLE source, DESCRIPTOR(ts), INTERVAL '5' MINUTES))          " +
                        "GROUP BY window_start, window_end                                               "
        );
    }
}
