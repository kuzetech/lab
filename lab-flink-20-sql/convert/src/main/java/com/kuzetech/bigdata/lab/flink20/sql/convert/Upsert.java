package com.kuzetech.bigdata.lab.flink20.sql.convert;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class Upsert {
    public static void main(String[] args) throws Exception {
        // create environments of both APIs
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // set the runtime mode
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // create a DataStream
        DataStream<Row> dataStream = env.fromElements(
                Row.of("Alice", 12),
                Row.of("Bob", 10),
                Row.of("Alice", 100));

        // interpret the insert-only DataStream as a Table
        Table inputTable = tableEnv.fromDataStream(dataStream).as("name", "score");

        // register the Table object as a view and query it
        // the query contains an aggregation that produces updates
        tableEnv.createTemporaryView("InputTable", inputTable);
        Table resultTable = tableEnv.sqlQuery(
                "SELECT name, SUM(score) FROM InputTable GROUP BY name");

        // interpret the updating Table as a changelog DataStream
        DataStream<Row> resultStream = tableEnv.toChangelogStream(resultTable);

        // add a printing sink and execute in DataStream API
        resultStream.print();
        env.execute();

        // prints in BATCH mode:
        // +I[Bob, 10]
        // +I[Alice, 112]

        // prints in STREAMING mode:
        // +I[Alice, 12]
        // +I[Bob, 10]
        // -U[Alice, 12]
        // +U[Alice, 112]
    }
}
