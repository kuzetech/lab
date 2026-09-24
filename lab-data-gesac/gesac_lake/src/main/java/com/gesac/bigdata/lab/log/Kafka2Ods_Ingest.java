package com.gesac.bigdata.lab.log;

import com.gesac.bigdata.lab.common.CatalogUtil;
import com.gesac.bigdata.lab.common.EnvironmentUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.paimon.catalog.Catalog;

public class  Kafka2Ods_Ingest {
    public static void main(String[] args) {
        StreamExecutionEnvironment env =
                EnvironmentUtil.generateStreamExecutionEnvironment("log_source_to_ods");
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        tableEnv.executeSql("""
                CREATE CATALOG hive_catalog WITH (
                  'type' = 'hive',
                  'hive-conf-dir' = 'src/main/resources'
                );
                """);

        tableEnv.executeSql("""
                USE CATALOG hive_catalog; 
                """);

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE source (
                  `event`   STRING,
                  `page`    STRING,
                  `ts`      BIGINT
                ) WITH (
                  'connector' = 'kafka',
                  'topic' = 'source_log',
                  'properties.bootstrap.servers' = 'localhost:9092',
                  'properties.group.id' = 'log_source_to_ods',
                  'scan.startup.mode' = 'earliest-offset',
                  'format' = 'json'
                )
                """);


        tableEnv.sqlQuery("""
                select * from source
                """).execute().print();




    }
}
