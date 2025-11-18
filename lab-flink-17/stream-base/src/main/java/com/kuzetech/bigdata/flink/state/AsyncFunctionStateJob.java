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

package com.kuzetech.bigdata.flink.state;

import com.kuzetech.bigdata.flink.udsource.GenTimeSingleParallelSource;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.configuration.StateBackendOptions;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;

public class AsyncFunctionStateJob {

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.set(RestOptions.BIND_PORT, "9999");
        config.set(StateBackendOptions.STATE_BACKEND, "rocksdb");
        config.set(CheckpointingOptions.CHECKPOINT_STORAGE, "filesystem");
        config.set(CheckpointingOptions.INCREMENTAL_CHECKPOINTS, true);
        config.set(ExecutionCheckpointingOptions.CHECKPOINTING_MODE, CheckpointingMode.EXACTLY_ONCE);
        config.set(ExecutionCheckpointingOptions.EXTERNALIZED_CHECKPOINT, CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        config.set(CheckpointingOptions.CHECKPOINTS_DIRECTORY, "file:///Users/huangsw/code/lab/lab-flink-17/data/cks");
        config.set(SavepointConfigOptions.SAVEPOINT_PATH, "file:///Users/huangsw/code/lab/lab-flink-17/data/cks/dec2531991b996fbc1ebb924f8d86795/chk-1");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.enableCheckpointing(5000);
        env.setParallelism(1);

        DataStreamSource<Long> sourceStream = env.addSource(new GenTimeSingleParallelSource());

        DataStream<Tuple3<Long, Long, Boolean>> resultStream =
                AsyncDataStream.orderedWait(sourceStream, new AsyncMockRequest2(), 1000, TimeUnit.MILLISECONDS, 100).uid("AsyncMockRequest1").name("AsyncMockRequest1");

        resultStream.print();

        env.execute("AsyncFunctionStateJob");
    }
}
