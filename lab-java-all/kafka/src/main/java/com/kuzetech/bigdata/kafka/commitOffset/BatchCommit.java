package com.kuzetech.bigdata.kafka.commitOffset;

import com.kuzetech.bigdata.kafka.commitOffset.callback.OffsetsCommitAsyncCallback;
import com.kuzetech.bigdata.kafka.commitOffset.exception.AsyncCommitOffsetsConsecutiveErrorException;
import com.kuzetech.bigdata.kafka.utils.ConsumerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
@Slf4j
public class BatchCommit {

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = ConsumerUtil.CreateManualCommit();
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
                    log.error("program will close", e);
                    break;
                } else {
                    log.error("consume message error", e);
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
