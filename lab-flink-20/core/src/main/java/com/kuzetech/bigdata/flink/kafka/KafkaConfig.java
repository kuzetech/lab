package com.kuzetech.bigdata.flink.kafka;

import com.kuzetech.bigdata.flink.base.JobConfig;
import lombok.Getter;
import lombok.Setter;
import org.apache.flink.api.java.utils.ParameterTool;


@Getter
@Setter
public class KafkaConfig extends JobConfig {

    public static final String DEFAULT_KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String DEFAULT_KAFKA_TRANSACTION_TIMEOUT_MS = "900000";

    private String bootstrapServers;
    private String sourceTopic;
    private String subscriber;
    private String clientIdPrefix;
    private String sinkTopic;
    private String transactionalIdPrefix;
    private String transactionTimeoutMs;

    /**
     * 订阅进度初始化位置
     * 样例值为 earliest 或 latest"
     * 分区指定待实现
     */
    private String startingOffsets;

    public KafkaConfig(ParameterTool parameterTool) {
        super(parameterTool);
    }


    public static KafkaConfig generateFromParameterTool(ParameterTool parameterTool) {
        KafkaConfig config = new KafkaConfig(parameterTool);
        config.setBootstrapServers(parameterTool.get("kafka.bootstrap.servers", DEFAULT_KAFKA_BOOTSTRAP_SERVERS));

        config.setSourceTopic(parameterTool.get("kafka.consumer.topic"));
        config.setSubscriber(parameterTool.get("kafka.consumer.subscriber"));
        config.setClientIdPrefix(parameterTool.get("kafka.consumer.client.id.prefix"));
        config.setStartingOffsets(parameterTool.get("kafka.consumer.starting.offsets"));

        config.setSinkTopic(parameterTool.get("kafka.producer.topic"));
        config.setTransactionalIdPrefix(parameterTool.get("kafka.producer.transactional.id.prefix"));
        config.setTransactionTimeoutMs(parameterTool.get("kafka.producer.transaction.timeout.ms", DEFAULT_KAFKA_TRANSACTION_TIMEOUT_MS));

        return config;
    }

}
