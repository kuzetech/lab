package com.kuzetech.bigdata.flink.utils;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkUtil {

    public static StreamExecutionEnvironment getEnvironment(String checkPointPath) {
        Configuration configuration = new Configuration();

        configuration.setString(RestOptions.BIND_PORT, "8081-9999");

        // 设置本地启动的 slot 数量
        // configuration.setInteger(TaskManagerOptions.NUM_TASK_SLOTS, 10);


        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);

        // 设置全局默认并行度
        env.setParallelism(2);

        ExecutionConfig executionConfig = env.getConfig();
        // use compression for the state snapshot data
        executionConfig.setUseSnapshotCompression(true);

        env.setStateBackend(new EmbeddedRocksDBStateBackend(true));

        // 设置 hadoop 用户
        System.setProperty("HADOOP_USER_NAME", "hadoop");
        env.enableCheckpointing(30 * 1000);
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        // 使用 FileSystemCheckpointStorage
        // hdfs://namenode:40010/flink/checkpoints
        // file:///data/flink/checkpoints
        checkpointConfig.setCheckpointStorage(checkPointPath);
        checkpointConfig.setMaxConcurrentCheckpoints(1);
        checkpointConfig.setCheckpointTimeout(10 * 30 * 1000);
        checkpointConfig.setMinPauseBetweenCheckpoints(5 * 1000);
        checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        checkpointConfig.setTolerableCheckpointFailureNumber(2);
        // 任务取消时依旧保留 checkpoint，注意需要手动清理
        checkpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        // 开启非对齐 Checkpoint 实验特性
        // checkpointConfig.enableUnalignedCheckpoints();

        return env;
    }

}
