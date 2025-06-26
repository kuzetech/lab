package com.kuzetech.bigdata.flink.base;

import lombok.Getter;
import lombok.Setter;
import org.apache.flink.api.java.utils.ParameterTool;

@Getter
@Setter
public class JobConfig {

    public static final String DEFAULT_JOB_NAME = "flink-default-job";
    public static final Integer DEFAULT_JOB_OUT_OF_ORDERNESS_SECOND = 60;

    private String jobName;
    private Integer jobOutOfOrdernessSecond;

    public JobConfig(ParameterTool parameterTool) {
        this.jobName = parameterTool.get("job.name", DEFAULT_JOB_NAME);
        this.jobOutOfOrdernessSecond = parameterTool.getInt("job.out.of.orderness.sec", DEFAULT_JOB_OUT_OF_ORDERNESS_SECOND);
    }
}
