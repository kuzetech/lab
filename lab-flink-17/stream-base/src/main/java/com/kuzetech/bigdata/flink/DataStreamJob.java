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

import com.kuzetech.bigdata.flink.udsource.StringNoParallelSource;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.configuration.StateBackendOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

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
public class DataStreamJob {

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.set(RestOptions.BIND_PORT, "9999");
        config.set(StateBackendOptions.STATE_BACKEND, "rocksdb");
        config.set(CheckpointingOptions.CHECKPOINT_STORAGE, "filesystem");
        config.set(CheckpointingOptions.INCREMENTAL_CHECKPOINTS, true);
        config.set(ExecutionCheckpointingOptions.CHECKPOINTING_MODE, CheckpointingMode.EXACTLY_ONCE);
        config.set(ExecutionCheckpointingOptions.EXTERNALIZED_CHECKPOINT, CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        config.set(CheckpointingOptions.CHECKPOINTS_DIRECTORY, "file:///Users/huangsw/code/lab/lab-java-all/flink17-steam/data/cks");
        // config.set(CheckpointingOptions.SAVEPOINT_DIRECTORY, "file:///Users/huangsw/code/lab/lab-java-all/flink17-steam/data/sps");
        // config.set(SavepointConfigOptions.SAVEPOINT_PATH, "file:///Users/huangsw/code/lab/lab-java-all/flink17-steam/data/cks/813c395039ca9da60a138146259d997b/chk-1");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.enableCheckpointing(10000);
        // StateBackend rocksDB = new RocksDBStateBackend("file:///Users/huangsw/code/lab/lab-java-all/flink17-steam/data/rocks", true);
        // env.setStateBackend(rocksDB);

        // Set parallelism and maxParallelism explicitly
        env.setParallelism(2); // e.g. 2 subtasks
        env.getConfig().setMaxParallelism(4); // total key groups = 4

        // Controlled keys that map to specific key groups
        env.addSource(new StringNoParallelSource()).setParallelism(1)
                .map((MapFunction<String, String>) value -> value)
                .keyBy(value -> value)
                .process(new KeyStateTracker())
                .print();

        env.execute("KeyGroupTestJob");
    }

    public static class KeyStateTracker extends KeyedProcessFunction<String, String, String> {
        private transient ValueState<Integer> visitCount;

        @Override
        public void open(Configuration parameters) {
            ValueStateDescriptor<Integer> desc =
                    new ValueStateDescriptor<>("visitCount", Integer.class, 0);
            visitCount = getRuntimeContext().getState(desc);
        }

        @Override
        public void processElement(String key, Context ctx, Collector<String> out) throws Exception {
            Integer current = visitCount.value();
            current++;
            visitCount.update(current);

            // Output the key, subtask id, and key group info
            int subtaskIndex = getRuntimeContext().getIndexOfThisSubtask();
            int maxParallelism = getRuntimeContext().getMaxNumberOfParallelSubtasks();
            int keyGroup = assignToKeyGroup(key, maxParallelism);

            out.collect(String.format("Subtask %d received key=%s (key group=%d, visitCount=%d)",
                    subtaskIndex, key, keyGroup, current));
        }

        private int assignToKeyGroup(String key, int maxParallelism) {
            return Math.abs(key.hashCode() % maxParallelism);
        }
    }
}
