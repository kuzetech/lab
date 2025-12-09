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

    public static final String DEFAULT_JOB_NAME = "default";
    public static final Integer DEFAULT_WATERMARK_OUT_INTERVAL = 60000;

    private String jobName;
    private Long watermarkOutInterval;
    private KafkaSourceConfig kafkaSourceConfig;
    private KafkaSinkConfig kafkaSinkConfig;
    private PulsarSourceConfig pulsarSourceConfig;
    private PulsarSinkConfig pulsarSinkConfig;

    public JobConfig(ParameterTool parameterTool) {
        this.jobName = parameterTool.get("job.name", DEFAULT_JOB_NAME);

        this.watermarkOutInterval = parameterTool.getLong("watermark.out.interval", DEFAULT_WATERMARK_OUT_INTERVAL);

        this.kafkaSourceConfig = KafkaSourceConfig.generateFromParameterTool(parameterTool);
        this.kafkaSinkConfig = KafkaSinkConfig.generateFromParameterTool(parameterTool);
        this.pulsarSourceConfig = PulsarSourceConfig.generateFromParameterTool(parameterTool);
        this.pulsarSinkConfig = PulsarSinkConfig.generateFromParameterTool(parameterTool);
    }
}
