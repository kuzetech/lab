package com.kuzetech.bigdata.kafka.consumers;

import com.kuzetech.bigdata.kafka.utils.ConsumerUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;

public class ReadCommittedConsumer {

    public static void main(String[] args) {
        try (KafkaConsumer<String, String> consumer = ConsumerUtil.CreateReadCommitted()) {
            consumer.subscribe(Arrays.asList("test"));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records)
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
        }
    }
}
