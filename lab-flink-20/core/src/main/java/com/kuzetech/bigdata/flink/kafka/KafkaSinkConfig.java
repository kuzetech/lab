package com.kuzetech.bigdata.flink.kafka;

import lombok.Getter;
import lombok.Setter;
import org.apache.flink.api.java.utils.ParameterTool;

import static com.kuzetech.bigdata.flink.kafka.Constants.DEFAULT_KAFKA_BOOTSTRAP_SERVERS;


@Getter
@Setter
public class KafkaSinkConfig {

    public static final String DEFAULT_KAFKA_TRANSACTION_TIMEOUT_MS = "900000";

    private String bootstrapServers;
    private String topic;
    private String transactionalIdPrefix;
    private String transactionTimeoutMs;

    public static KafkaSinkConfig generateFromParameterTool(ParameterTool parameterTool) {
        KafkaSinkConfig config = new KafkaSinkConfig();
        config.setBootstrapServers(parameterTool.get("kafka.bootstrap.servers", DEFAULT_KAFKA_BOOTSTRAP_SERVERS));

        config.setTopic(parameterTool.get("kafka.producer.topic"));
        config.setTransactionalIdPrefix(parameterTool.get("kafka.producer.transactional.id.prefix"));
        config.setTransactionTimeoutMs(parameterTool.get("kafka.producer.transaction.timeout.ms", DEFAULT_KAFKA_TRANSACTION_TIMEOUT_MS));

        return config;
    }

}
