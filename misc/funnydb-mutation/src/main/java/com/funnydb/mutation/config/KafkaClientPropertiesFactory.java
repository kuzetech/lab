package com.funnydb.mutation.config;

import java.util.Properties;

public final class KafkaClientPropertiesFactory {
    private KafkaClientPropertiesFactory() {
    }

    public static Properties buildConsumerProperties(MutationAppConfig config, String clientId) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", config.getKafkaBootstrapServers());
        properties.setProperty("group.id", config.getKafkaConsumerGroupId());
        properties.setProperty("client.id", clientId);
        properties.setProperty("enable.auto.commit", "false");
        properties.setProperty("isolation.level", "read_committed");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return properties;
    }

    public static Properties buildProducerProperties(MutationAppConfig config, String clientId) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", config.getKafkaBootstrapServers());
        properties.setProperty("client.id", clientId);
        properties.setProperty("acks", "all");
        properties.setProperty("enable.idempotence", "true");
        properties.setProperty("transactional.id", config.getKafkaProducerTransactionalIdPrefix() + "-" + clientId);
        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return properties;
    }
}
