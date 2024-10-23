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

package com.kuzetech.bigdata.flink17;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Skeleton for a Flink DataStream Job.
 *
 * <p>For a tutorial how to write a Flink application, check the
 * tutorials and examples on the <a href="https://flink.apache.org">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class App {

    public static void main(String[] args) throws Exception {
        // Sets up the execution environment, which is the main entry point
        // to building Flink applications.
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 指定时间语义，1.12 版本后默认使用事件事件，废弃该方法
        // env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        // 重启策略
        // env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.seconds(5)));

        // stateBackend 管理
        // env.setStateBackend()

        // checkpoint 管理
        // env.enableCheckpointing(5000);

        // 序列化器管理
        // env.addDefaultKryoSerializer();

        // 类型注册
        // env.registerType();
        // env.registerTypeWithKryoSerializer();

        // 获取 StreamGraph
        // env.getStreamGraph();

        // 注册缓存文件，可以是本地文件，也可以是分布书文件，例如 hdfs
        // env.registerCachedFile("path", "unique name");

        // 自带基本数据源
        // env.addSource();
        // env.fromCollection();
        // env.fromElements();
        // env.socketTextStream();
        // env.readTextFile(); 被 FileSource 替代，需要引入新的依赖包

        // 自定义数据源
        // env.addSource()

        // 任务的提交和运行
        // Execute program, beginning computation.
        env.execute("Flink Java API Skeleton1");
        env.executeAsync("Flink Java API Skeleton2");

        /*
         * Here, you can start creating your execution plan for Flink.
         *
         * Start with getting some data from the environment, like
         * 	env.fromSequence(1, 10);
         *
         * then, transform the resulting DataStream<Long> using operations
         * like
         * 	.filter()
         * 	.flatMap()
         * 	.window()
         * 	.process()
         *
         * and many more.
         * Have a look at the programming guide:
         *
         * https://nightlies.apache.org/flink/flink-docs-stable/
         *
         */

        /*
         * 主要的转换操作
         * 1. 基于单条记录 filter map
         * 2. 基于窗口 window
         * 3. 合并多条流 union join connect
         * 4. 拆分多条流 split
         *
         * */
    }
}
