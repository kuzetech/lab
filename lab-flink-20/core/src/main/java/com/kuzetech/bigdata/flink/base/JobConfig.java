package com.kuzetech.bigdata.flink.base;

import lombok.Getter;
import lombok.Setter;
import org.apache.flink.api.java.utils.ParameterTool;

@Getter
@Setter
public class JobConfig {
    public static final String DEFAULT_JOB_NAME = "flink-default-job";

    private String jobName;

    public JobConfig(ParameterTool parameterTool) {
        this.jobName = parameterTool.get("job.name", DEFAULT_JOB_NAME);
    }
}
