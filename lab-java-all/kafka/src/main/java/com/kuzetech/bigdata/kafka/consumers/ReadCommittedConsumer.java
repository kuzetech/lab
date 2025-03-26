package com.kuzetech.bigdata.kafka.consumers;

import com.kuzetech.bigdata.kafka.utils.ConsumerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
public class ReadCommittedConsumer {

    public static void main(String[] args) {
        try (KafkaConsumer<String, String> consumer = ConsumerUtil.CreateReadCommitted()) {
            consumer.subscribe(Arrays.asList("funnydb-ingest-receive"));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records)
                    log.info("offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
            }
        }
    }
}
