package com.kuzetech.bigdata.kafka.commitManual;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Hello world!
 */
public class BatchCommitConsumer {

    public static final Logger logger = LoggerFactory.getLogger(BatchCommitConsumer.class);

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1000 * 60 * 5);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        int count = 0;

        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    // 处理消息 process(record)

                    offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
                    if (count % 100 == 0) {
                        consumer.commitAsync(offsets, new OffsetsCommitAsyncCallback());
                    }
                    count++;
                }
            } catch (Exception e) {
                if (e instanceof AsyncCommitOffsetsConsecutiveErrorException) {
                    logger.error("program will close", e);
                    break;
                } else {
                    logger.error("consume message error", e);
                }
            } finally {
                try {
                    consumer.commitSync();
                } finally {
                    consumer.close();
                }
            }
        }


    }


}
