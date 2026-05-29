package com.kuzetech.bigdata.lab.flink20.sql.core.util;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.StateBackendOptions;
import org.apache.flink.core.execution.CheckpointingMode;
import org.apache.flink.table.api.EnvironmentSettings;

import java.nio.file.Path;

public class EnvironmentSettingsUtil {
    public static EnvironmentSettings getEnvironmentSettings() {
        return EnvironmentSettings.newInstance()
                .inStreamingMode()
                .build();
    }

    public static EnvironmentSettings getCheckPointEnvironmentSettings(ParameterTool parameter) {

        Configuration configuration = new Configuration();
        configuration.setString(CheckpointingOptions.CHECKPOINTING_INTERVAL.key(), parameter.get(CheckpointingOptions.CHECKPOINTING_INTERVAL.key(), "10s"));
        configuration.setString(CheckpointingOptions.CHECKPOINTING_TIMEOUT.key(), parameter.get(CheckpointingOptions.CHECKPOINTING_TIMEOUT.key(), "60s"));
        configuration.setString(CheckpointingOptions.CHECKPOINTING_CONSISTENCY_MODE.key(), parameter.get(CheckpointingOptions.CHECKPOINTING_CONSISTENCY_MODE.key(), CheckpointingMode.EXACTLY_ONCE.name()));
        configuration.setString(CheckpointingOptions.CHECKPOINT_STORAGE.key(), parameter.get(CheckpointingOptions.CHECKPOINT_STORAGE.key(), "filesystem"));
        configuration.setString(CheckpointingOptions.CHECKPOINTS_DIRECTORY.key(), parameter.get(CheckpointingOptions.CHECKPOINTS_DIRECTORY.key(), Path.of("checkpoints").toAbsolutePath().toUri().toString()));
        configuration.setString(StateBackendOptions.STATE_BACKEND.key(), parameter.get(StateBackendOptions.STATE_BACKEND.key(), "rocksdb"));

        return EnvironmentSettings.newInstance()
                .inStreamingMode()
                .withConfiguration(configuration)
                .build();
    }


}
