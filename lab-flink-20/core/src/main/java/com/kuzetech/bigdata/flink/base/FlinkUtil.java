package com.kuzetech.bigdata.flink.base;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.StateBackendOptions;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static com.kuzetech.bigdata.flink.base.FlinkConstant.CHECKPOINT_STORAGE_TYPE_FILESYSTEM;
import static com.kuzetech.bigdata.flink.base.FlinkConstant.STATE_BACKEND_TYPE_ROCKSDB;
import static org.apache.flink.configuration.CoreOptions.DEFAULT_PARALLELISM;
import static org.apache.flink.configuration.RestOptions.BIND_PORT;
import static org.apache.flink.configuration.StateRecoveryOptions.SAVEPOINT_IGNORE_UNCLAIMED_STATE;
import static org.apache.flink.configuration.StateRecoveryOptions.SAVEPOINT_PATH;
import static org.apache.flink.configuration.TaskManagerOptions.NUM_TASK_SLOTS;

public class FlinkUtil {
    public static StreamExecutionEnvironment initEnv(ParameterTool parameterTool) {
        Configuration config = new Configuration();
        if (parameterTool.has(CheckpointingOptions.CHECKPOINTS_DIRECTORY.key())) {
            config.set(CheckpointingOptions.CHECKPOINT_STORAGE, CHECKPOINT_STORAGE_TYPE_FILESYSTEM);
            config.set(StateBackendOptions.STATE_BACKEND, STATE_BACKEND_TYPE_ROCKSDB);
            config.set(CheckpointingOptions.CHECKPOINTS_DIRECTORY, parameterTool.get(CheckpointingOptions.CHECKPOINTS_DIRECTORY.key()));
            config.set(ExecutionCheckpointingOptions.CHECKPOINTING_MODE, CheckpointingMode.EXACTLY_ONCE);
            config.set(ExecutionCheckpointingOptions.EXTERNALIZED_CHECKPOINT, CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        }
        if (parameterTool.has(SAVEPOINT_PATH.key())) {
            config.set(SavepointConfigOptions.SAVEPOINT_PATH, parameterTool.get(SAVEPOINT_PATH.key()));
        }
        if (parameterTool.has(SAVEPOINT_IGNORE_UNCLAIMED_STATE.key())) {
            config.set(SavepointConfigOptions.SAVEPOINT_IGNORE_UNCLAIMED_STATE, parameterTool.getBoolean(SAVEPOINT_IGNORE_UNCLAIMED_STATE.key()));
        }
        if (parameterTool.has(NUM_TASK_SLOTS.key())) {
            config.set(NUM_TASK_SLOTS, parameterTool.getInt(NUM_TASK_SLOTS.key()));
        }
        if (parameterTool.has(BIND_PORT.key())) {
            config.set(BIND_PORT, parameterTool.get(BIND_PORT.key()));
        }

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        if (parameterTool.has(DEFAULT_PARALLELISM.key())) {
            env.setParallelism(parameterTool.getInt(DEFAULT_PARALLELISM.key()));
        }
        if (parameterTool.has(CheckpointingOptions.CHECKPOINTING_INTERVAL.key())) {
            env.enableCheckpointing(parameterTool.getLong(CheckpointingOptions.CHECKPOINTING_INTERVAL.key()));
        }

        return env;
    }
}
