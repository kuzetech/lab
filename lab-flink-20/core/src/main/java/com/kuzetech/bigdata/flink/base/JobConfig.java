package com.kuzetech.bigdata.flink.base;

import com.kuzetech.bigdata.flink.kafka.KafkaSinkConfig;
import com.kuzetech.bigdata.flink.kafka.KafkaSourceConfig;
import com.kuzetech.bigdata.flink.pulsar.PulsarSinkConfig;
import com.kuzetech.bigdata.flink.pulsar.PulsarSourceConfig;
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
    private KafkaSourceConfig kafkaSourceConfig;
    private KafkaSinkConfig kafkaSinkConfig;
    private PulsarSourceConfig pulsarSourceConfig;
    private PulsarSinkConfig pulsarSinkConfig;

    public JobConfig(ParameterTool parameterTool) {
        this.jobName = parameterTool.get("job.name", DEFAULT_JOB_NAME);
        this.jobOutOfOrdernessSecond = parameterTool.getInt("job.out.of.orderness.sec", DEFAULT_JOB_OUT_OF_ORDERNESS_SECOND);

        this.kafkaSourceConfig = KafkaSourceConfig.generateFromParameterTool(parameterTool);
        this.kafkaSinkConfig = KafkaSinkConfig.generateFromParameterTool(parameterTool);
        this.pulsarSourceConfig = PulsarSourceConfig.generateFromParameterTool(parameterTool);
        this.pulsarSinkConfig = PulsarSinkConfig.generateFromParameterTool(parameterTool);
    }
}
