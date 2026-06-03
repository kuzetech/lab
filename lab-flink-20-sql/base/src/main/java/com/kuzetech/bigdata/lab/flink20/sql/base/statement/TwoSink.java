package com.kuzetech.bigdata.lab.flink20.sql.base.statement;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class TwoSink {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE source (
                    name STRING
                ) WITH (
                    'connector' = 'datagen'
                )
                """);

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE sink1 (
                    name STRING
                ) WITH (
                    'connector' = 'print'
                )
                """);

        tableEnv.executeSql("""
                CREATE TEMPORARY TABLE sink2 (
                    name STRING
                ) WITH (
                    'connector' = 'print'
                )
                """);

        tableEnv.createStatementSet()
                .addInsertSql("INSERT INTO sink1 SELECT * FROM source")
                .addInsertSql("INSERT INTO sink2 SELECT * FROM source")
                .execute();

    }
}
