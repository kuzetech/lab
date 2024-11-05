/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuzetech.bigdata.flink20;

import org.apache.flink.connector.datagen.table.DataGenConnectorOptions;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamStatementSet;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class CreateTableSink {

    public static void main(String[] args) throws Exception {
        StreamTableEnvironment tEnv = LaunchUtil.GetDefaultStreamTableEnvironment();

        // Using table descriptors
        final TableDescriptor sourceDescriptor = TableDescriptor.forConnector("datagen")
                .schema(Schema.newBuilder()
                        .column("name", DataTypes.STRING())
                        .column("age", DataTypes.INT())
                        .build())
                .option(DataGenConnectorOptions.ROWS_PER_SECOND, 1L)
                .build();

        tEnv.createTable("SourceTable", sourceDescriptor);

        Table sourceTable = tEnv.from("SourceTable");

        tEnv.executeSql(
                "CREATE TABLE SinkTable (\n" +
                        " name STRING,\n" +
                        " age INT\n" +
                        ") WITH (\n" +
                        " 'connector' = 'print'\n" +
                        ")"
        );

        // 使用 table api 写入
        TablePipeline pipeline = sourceTable.insertInto("SinkTable");
        // 立刻执行语句
        pipeline.execute();

        // 使用 sql api 写入
        // 该语句会立马被提交
        TableResult result = tEnv.executeSql(
                "INSERT INTO SinkTable " +
                        "SELECT name, age " +
                        "FROM SourceTable "
        );


        // 上述的写法每个 pipeline 只会优化自己的语句，另一方面 web 只会展示第一个提交的任务

        // 当我们有多个 pipeline 时应该采用如下写法
        // StreamStatementSet 把所有的 pipeline 集中到一起优化，并且一起提交任务
        StreamStatementSet statSet = tEnv.createStatementSet();
        statSet.add(pipeline);
        statSet.addInsertSql(
                "INSERT INTO SinkTable " +
                        "SELECT name, age " +
                        "FROM SourceTable "
        );

        TableResult statSetResult = statSet.execute();

    }
}
