package com.kuzetech.bigdata.flink.pulsar;

import com.kuzetech.bigdata.flink.base.JobConfig;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.api.java.utils.ParameterTool;


@Getter
@Setter
public class PulsarConfig extends JobConfig {

    public static final String DEFAULT_PULSAR_SERVICE_URL = "pulsar://localhost:6650";
    public static final String DEFAULT_PULSAR_ADMIN_URL = "http://localhost:8080";

    private String serviceUrl;
    private String adminUrl;
    private String sourceTopic;
    private String subscriber;
    private String sinkTopic;
    private String producerName;

    /**
     * pulsar 订阅进度初始化位置
     * 样例值为 earliest 或 latest，也可以具体指定分区进度 3727:17363:0,3749:4465:1"
     */
    private String startCursor;

    public PulsarConfig(ParameterTool parameterTool) {
        super(parameterTool);
    }


    public static PulsarConfig generateFromParameterTool(ParameterTool parameterTool) {
        PulsarConfig config = new PulsarConfig(parameterTool);
        config.setServiceUrl(parameterTool.get("pulsar.service.url", DEFAULT_PULSAR_SERVICE_URL));
        config.setAdminUrl(parameterTool.get("pulsar.admin.url", DEFAULT_PULSAR_ADMIN_URL));

        config.setSourceTopic(parameterTool.get("pulsar.consumer.topic"));
        config.setSubscriber(parameterTool.get("pulsar.consumer.subscriber"));
        config.setStartCursor(parameterTool.get("pulsar.consumer.start.cursor"));

        config.setSinkTopic(parameterTool.get("pulsar.producer.topic"));
        config.setProducerName(parameterTool.get("pulsar.producer.name"));
        return config;
    }

}
