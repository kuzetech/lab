package com.kuzetech.bigdata.flink.kafka;

import lombok.Getter;
import lombok.Setter;
import org.apache.flink.api.java.utils.ParameterTool;


@Getter
@Setter
public class KafkaSourceConfig {

    private String bootstrapServers;
    private String topic;
    private String subscriber;
    private String clientIdPrefix;

    /**
     * 订阅进度初始化位置
     * 样例值为 earliest 或 latest"
     * 分区指定待实现
     */
    private String startingOffsets;

    public static KafkaSourceConfig generateFromParameterTool(ParameterTool parameterTool) {
        KafkaSourceConfig config = new KafkaSourceConfig();
        config.setBootstrapServers(parameterTool.get("kafka.bootstrap.servers", Constants.DEFAULT_KAFKA_BOOTSTRAP_SERVERS));

        config.setTopic(parameterTool.get("kafka.consumer.topic"));
        config.setSubscriber(parameterTool.get("kafka.consumer.subscriber"));
        config.setClientIdPrefix(parameterTool.get("kafka.consumer.client.id.prefix"));
        config.setStartingOffsets(parameterTool.get("kafka.consumer.starting.offsets"));

        return config;
    }

}
