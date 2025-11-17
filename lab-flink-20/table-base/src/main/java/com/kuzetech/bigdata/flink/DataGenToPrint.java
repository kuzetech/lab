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

package com.kuzetech.bigdata.flink;

import org.apache.flink.connector.datagen.table.DataGenConnectorOptions;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class DataGenToPrint {

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
                "CREATE TEMPORARY TABLE PrintTable (\n" +
                        " f0 INT,\n" +
                        " f1 STRING\n" +
                        ") WITH (\n" +
                        " 'connector' = 'print'\n" +
                        ")"
        );

        Table genTable = tableEnv.from("GenTable");

        TablePipeline pipeline = genTable.insertInto("PrintTable");

        TableResult result = pipeline.execute();
    }
}
