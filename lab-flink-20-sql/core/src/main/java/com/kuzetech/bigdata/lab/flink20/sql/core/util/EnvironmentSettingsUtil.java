package com.kuzetech.bigdata.lab.flink20.sql.core.util;

import org.apache.flink.table.api.EnvironmentSettings;

public class EnvironmentSettingsUtil {
    public static EnvironmentSettings getEnvironmentSettings() {
        return EnvironmentSettings.newInstance()
                .inStreamingMode()
                .build();
    }

    public static EnvironmentSettings getCheckPointEnvironmentSettings() {
        return EnvironmentSettings.newInstance()
                .inStreamingMode()
                .withConfiguration(ConfigurationUtil.generateDefaultCheckpointConfiguration())
                .build();
    }


}
