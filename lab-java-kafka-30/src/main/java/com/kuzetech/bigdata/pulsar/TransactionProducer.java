package com.kuzetech.bigdata.pulsar;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class TransactionProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 1. 设置事务 ID（必须唯一）
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my-transactional-id-001");
        // 2. 确保幂等性和 Acks
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 3. 初始化事务
        producer.initTransactions();

        try {
            int i = 0;
            while (true) {
                try {
                    // 4. 开启事务
                    producer.beginTransaction();

                    String message = "Message-" + i;
                    producer.send(new ProducerRecord<>("source", message));

                    System.out.println("Sending: " + message);

                    // 5. 提交事务
                    producer.commitTransaction();

                    i++;
                    // 每秒发送一条
                    Thread.sleep(1000);

                } catch (Exception e) {
                    // 6. 发生异常时中止事务
                    producer.abortTransaction();
                    e.printStackTrace();
                }
            }
        } finally {
            producer.close();
        }
    }
}
