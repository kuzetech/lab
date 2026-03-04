package com.kuzetech.bigdata.pulsar;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class TransactionConsumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        // 配置 Bootstrap 服务器
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // 配置消费者组 ID
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-consumer-group");
        // 配置键和值的反序列化器
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // 配置隔离级别为读已提交（只读取已提交的事务消息）
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        // 自动提交偏移量
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // 订阅主题
        consumer.subscribe(Collections.singletonList("source"));

        try {
            while (true) {
                // 拉取消息
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Received: key=" + record.key() +
                            ", value=" + record.value() +
                            ", partition=" + record.partition() +
                            ", offset=" + record.offset());
                }
            }
        } finally {
            consumer.close();
        }
    }
}
