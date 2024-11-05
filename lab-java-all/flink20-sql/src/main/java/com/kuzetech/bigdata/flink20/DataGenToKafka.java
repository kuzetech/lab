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

public class DataGenToKafka {

    public static void main(String[] args) throws Exception {
        StreamTableEnvironment tableEnv = LaunchUtil.GetDefaultStreamTableEnvironment();

        tableEnv.createTemporaryTable("GenTable", TableDescriptor.forConnector("datagen")
                .schema(Schema.newBuilder()
                        .column("f0", DataTypes.INT())
                        .column("f1", DataTypes.STRING())
                        .build())
                .option(DataGenConnectorOptions.ROWS_PER_SECOND, 1L)
                .build());

        tableEnv.executeSql(
                "CREATE TEMPORARY TABLE NormalKafkaTable (\n" +
                        "  `f0` INT,\n" +
                        "  `f1` STRING\n" +
                        ") WITH (\n" +
                        "  'connector' = 'kafka',\n" +
                        "  'topic' = 'normal',\n" +
                        "  'properties.bootstrap.servers' = 'localhost:9092',\n" +
                        "  'properties.allow.auto.create.topics' = 'true',\n" +
                        "  'properties.group.id' = 'testGroup',\n" +
                        "  'scan.startup.mode' = 'earliest-offset',\n" +
                        "  'format' = 'json'\n" +
                        ")"
        );

        tableEnv.executeSql(
                "CREATE TEMPORARY TABLE UpsertKafkaTable (\n" +
                        "  f0 INT,\n" +
                        "  f1 STRING,\n" +
                        "  PRIMARY KEY (f0) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "  'connector' = 'upsert-kafka',\n" +
                        "  'topic' = 'upsert',\n" +
                        "  'properties.bootstrap.servers' = 'localhost:9092',\n" +
                        "  'properties.allow.auto.create.topics' = 'true',\n" +
                        "  'key.format' = 'json',\n" +
                        "  'value.format' = 'json'\n" +
                        ");"
        );

        Table genTable = tableEnv.from("GenTable");

        TablePipeline normalPipeline = genTable.insertInto("NormalKafkaTable");
        TablePipeline upsertPipeline = genTable.insertInto("UpsertKafkaTable");

        StreamStatementSet statementSet = tableEnv.createStatementSet();
        statementSet.add(normalPipeline);
        statementSet.add(upsertPipeline);
        statementSet.execute();

    }
}
