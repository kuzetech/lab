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

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.config.TableConfigOptions;

public class CreateTableEnvironment {

    public static void main(String[] args) throws Exception {

        // 正常创建时使用 TableEnvironment 即可满足日常使用
        // 如果需要在 DataStream 和 Table 之间互相转换才需要使用 StreamTableEnvironment

        Configuration configuration = new Configuration();
        configuration.set(TableConfigOptions.TABLE_CATALOG_NAME, "test");

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                //.inStreamingMode()
                //.inBatchMode()
                //.withBuiltInCatalogName()
                //.withBuiltInDatabaseName()
                //.withCatalogStore()
                .withConfiguration(configuration)
                .build();
        TableEnvironment tEnv = TableEnvironment.create(settings);
        // 未指定时，默认使用的 catalog 和 database
        tEnv.useCatalog("default_catalog");
        tEnv.useDatabase("default_database");


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment stEnv = StreamTableEnvironment.create(env);
        stEnv.useCatalog("default_catalog");
        stEnv.useDatabase("default_database");

    }
}
