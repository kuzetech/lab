package com.kuzetech.bigdata.flink.pulsar;

import lombok.Getter;
import lombok.Setter;
import org.apache.flink.api.java.utils.ParameterTool;

import static com.kuzetech.bigdata.flink.pulsar.Constants.DEFAULT_PULSAR_ADMIN_URL;
import static com.kuzetech.bigdata.flink.pulsar.Constants.DEFAULT_PULSAR_SERVICE_URL;


@Getter
@Setter
public class PulsarSourceConfig {

    private String serviceUrl;
    private String adminUrl;
    private String topic;
    private String subscriber;

    /**
     * 订阅进度初始化位置
     * 样例值为 earliest 或 latest，也可以具体指定分区进度 3727:17363:0,3749:4465:1"
     */
    private String startCursor;

    public static PulsarSourceConfig generateFromParameterTool(ParameterTool parameterTool) {
        PulsarSourceConfig config = new PulsarSourceConfig();
        config.setServiceUrl(parameterTool.get("pulsar.service.url", DEFAULT_PULSAR_SERVICE_URL));
        config.setAdminUrl(parameterTool.get("pulsar.admin.url", DEFAULT_PULSAR_ADMIN_URL));

        config.setTopic(parameterTool.get("pulsar.consumer.topic"));
        config.setSubscriber(parameterTool.get("pulsar.consumer.subscriber"));
        config.setStartCursor(parameterTool.get("pulsar.consumer.start.cursor"));

        return config;
    }

}
