package com.kuzetech.bigdata.flink.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkUtil {

    public static StreamExecutionEnvironment getEnvironment() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        assert stackTrace.length >= 3;
        // xxxx.java
        String callerClassFileName = stackTrace[2].getFileName(); // 索引 2 是调用 printCallerClassName 的方法
        assert callerClassFileName != null;
        return getEnvironment(callerClassFileName.split("\\.")[0], 1, null);
    }

    public static StreamExecutionEnvironment getEnvironment(String jobName) {
        return getEnvironment(jobName, 1, null);
    }

    public static StreamExecutionEnvironment getEnvironment(String jobName, Integer parallelism) {
        return getEnvironment(jobName, parallelism, null);
    }

    public static StreamExecutionEnvironment getEnvironment(String jobName, Integer parallelism, String recoverCheckpointPath) {
        Configuration configuration = new Configuration();

        configuration.setString(RestOptions.BIND_PORT, "8081-9999");

        if (StringUtils.isNotEmpty(recoverCheckpointPath)) {
            configuration.setString("execution.savepoint.path", recoverCheckpointPath);
            // configuration.setBoolean("state.backend.allowNonRestoredState", true);
        }

        // 设置本地启动的 slot 数量
        // configuration.setInteger(TaskManagerOptions.NUM_TASK_SLOTS, 10);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);

        // 设置 hadoop 用户
        System.setProperty("HADOOP_USER_NAME", "hadoop");

        // 设置全局默认并行度
        env.setParallelism(parallelism);
        env.enableCheckpointing(15 * 1000);
        env.setStateBackend(new EmbeddedRocksDBStateBackend(true));

        ExecutionConfig executionConfig = env.getConfig();
        executionConfig.setUseSnapshotCompression(true);

        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        // 使用 FileSystemCheckpointStorage
        // hdfs://namenode:40010/flink/checkpoints
        // file:///data/flink/checkpoints
        checkpointConfig.setCheckpointStorage("file:///Users/huangsw/code/lab/lab-java-all/flink/checkpoints/" + jobName);
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
