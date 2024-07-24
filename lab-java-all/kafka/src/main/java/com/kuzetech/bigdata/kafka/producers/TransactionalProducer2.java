package com.kuzetech.bigdata.kafka.producers;

import com.kuzetech.bigdata.kafka.utils.ProducerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

@Slf4j
public class TransactionalProducer2 {

    public static void main(String[] args) throws InterruptedException {
        try (KafkaProducer<String, String> producer = ProducerUtil.CreateTransactional("test2")) {
            producer.initTransactions();
            producer.beginTransaction();
            producer.send(new ProducerRecord<>("test", "test2"), new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    log.info("发送成功");
                }
            });
            producer.commitTransaction();
            log.info("程序结束");
        }
    }
}
