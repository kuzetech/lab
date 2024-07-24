package com.kuzetech.bigdata.kafka.utils;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

public class ConsumerUtil {

    private static Properties GenerateNormalProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");

        props.put("session.timeout.ms", 6 * 1000);
        props.put("heartbeat.interval.ms", 2 * 1000);
        props.put("max.poll.interval.ms", 5 * 60 * 1000);

        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return props;
    }

    public static KafkaConsumer<String, String> CreateAutoCommit() {
        Properties props = GenerateNormalProperties();
        props.put("enable.auto.commit", true);
        props.put("auto.commit.interval.ms", 5000);
        return new KafkaConsumer<>(props);
    }

    public static KafkaConsumer<String, String> CreateManualCommit() {
        Properties props = GenerateNormalProperties();
        props.put("enable.auto.commit", false);
        return new KafkaConsumer<>(props);
    }
}
