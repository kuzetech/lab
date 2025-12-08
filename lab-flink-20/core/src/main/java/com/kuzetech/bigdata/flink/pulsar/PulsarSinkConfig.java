package com.kuzetech.bigdata.flink.pulsar;

import lombok.Getter;
import lombok.Setter;
import org.apache.flink.api.java.utils.ParameterTool;

import static com.kuzetech.bigdata.flink.pulsar.Constants.DEFAULT_PULSAR_ADMIN_URL;
import static com.kuzetech.bigdata.flink.pulsar.Constants.DEFAULT_PULSAR_SERVICE_URL;


@Getter
@Setter
public class PulsarSinkConfig {

    private String serviceUrl;
    private String adminUrl;
    private String topic;
    private String producerName;

    public static PulsarSinkConfig generateFromParameterTool(ParameterTool parameterTool) {
        PulsarSinkConfig config = new PulsarSinkConfig();
        config.setServiceUrl(parameterTool.get("pulsar.service.url", DEFAULT_PULSAR_SERVICE_URL));
        config.setAdminUrl(parameterTool.get("pulsar.admin.url", DEFAULT_PULSAR_ADMIN_URL));

        config.setTopic(parameterTool.get("pulsar.producer.topic"));
        config.setProducerName(parameterTool.get("pulsar.producer.name"));
        return config;
    }

}
