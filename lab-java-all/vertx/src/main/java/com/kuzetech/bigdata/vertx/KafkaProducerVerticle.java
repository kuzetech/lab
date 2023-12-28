package com.kuzetech.bigdata.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.kafka.client.producer.KafkaProducer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public class KafkaProducerVerticle extends AbstractVerticle {

    private KafkaProducer<String, String> producer;

    @Override
    public void start() throws Exception {
        Map<String, String> producerConfig = new HashMap<>();
        producerConfig.put("bootstrap.servers", "localhost:9092");
        producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("acks", "1");

        producer = KafkaProducer.create(vertx, producerConfig);
    }

    @Override
    public void stop() throws Exception {
        producer.close();
    }
}
