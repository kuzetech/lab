package com.kuzetech.bigdata.kafka.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerUtil {

    private static Properties GenerateNormalProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return props;
    }

    public static KafkaProducer<String, String> CreateTransactional(String id) {
        Properties props = GenerateNormalProperties();
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, id);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 60 * 1000);
        return new KafkaProducer<String, String>(props);
    }
}
