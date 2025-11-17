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
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

public class CreateTableOrView {

    public static void main(String[] args) throws Exception {
        StreamTableEnvironment tEnv = LaunchUtil.GetDefaultStreamTableEnvironment();

        // Using table descriptors
        final TableDescriptor sourceDescriptor = TableDescriptor.forConnector("datagen")
                .schema(Schema.newBuilder()
                        .column("name", DataTypes.STRING())
                        .column("age", DataTypes.INT())
                        .build())
                .option(DataGenConnectorOptions.ROWS_PER_SECOND, 100L)
                .build();

        // 在 catalog 中创建表，如果是外部的 catalog 比如 Hive Metastore，该表将会被持久化
        tEnv.createTable("SourceTableA", sourceDescriptor);
        // 在 catalog 中创建临时表，即使是外部的 catalog 比如 Hive Metastore，该表也不会被持久化
        tEnv.createTemporaryTable("SourceTableB", sourceDescriptor);

        // 可以通过 createTemporaryView 创建一些复用的子查询
        // 使用 Table Api 进行查询
        Table childQueryTable1 = tEnv.from("SourceTableA")
                .filter($("age").isLessOrEqual(10))
                .select($("name"), $("age"));
        tEnv.createTemporaryView("ChildQuery1", childQueryTable1);

        // 使用 SQL api 进行查询
        Table childQueryTable2 = tEnv.sqlQuery(
                "SELECT name, age " +
                        "FROM SourceTableA " +
                        "WHERE age >= 10 "
        );
        tEnv.createTemporaryView("ChildQuery2", childQueryTable2);

    }
}
