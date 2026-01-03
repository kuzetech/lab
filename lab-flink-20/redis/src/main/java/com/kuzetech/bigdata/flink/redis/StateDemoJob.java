package com.kuzetech.bigdata.flink.redis;

import com.kuzetech.bigdata.flink.redis.func.StateProcessFunc;
import com.kuzetech.bigdata.flink.source.CountSingleParallelSource;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.StateBackendOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import static com.kuzetech.bigdata.flink.base.FlinkConstant.CHECKPOINT_STORAGE_TYPE_FILESYSTEM;
import static com.kuzetech.bigdata.flink.base.FlinkConstant.STATE_BACKEND_TYPE_ROCKSDB;
import static org.apache.flink.configuration.RestOptions.BIND_PORT;
import static org.apache.flink.configuration.TaskManagerOptions.NUM_TASK_SLOTS;

public class StateDemoJob {
    public static void main(String[] args) throws Exception {

        Configuration config = new Configuration();
        config.set(NUM_TASK_SLOTS, 6);
        config.set(BIND_PORT, "9987");
        config.set(CheckpointingOptions.CHECKPOINT_STORAGE, CHECKPOINT_STORAGE_TYPE_FILESYSTEM);
        config.set(StateBackendOptions.STATE_BACKEND, STATE_BACKEND_TYPE_ROCKSDB);
        config.set(ExecutionCheckpointingOptions.CHECKPOINTING_MODE, CheckpointingMode.EXACTLY_ONCE);
        config.set(ExecutionCheckpointingOptions.EXTERNALIZED_CHECKPOINT, CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //config.set(SavepointConfigOptions.SAVEPOINT_PATH, "");
        config.set(CheckpointingOptions.CHECKPOINTS_DIRECTORY, "file:///Users/kuze/code/lab/lab-flink-20/checkpoints");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);
        env.setParallelism(2);
        env.enableCheckpointing(5000);

        env.addSource(new CountSingleParallelSource())
                .map(Object::toString)
                .process(new StateProcessFunc())
                .print();

        env.execute();
    }
}
