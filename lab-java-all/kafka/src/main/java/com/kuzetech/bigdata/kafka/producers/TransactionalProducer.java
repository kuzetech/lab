package com.kuzetech.bigdata.kafka.producers;

import com.kuzetech.bigdata.kafka.utils.ProducerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TransactionalProducer {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        KafkaProducer<String, String> producer = ProducerUtil.CreateTransactional("test");

        producer.initTransactions();
        producer.beginTransaction();
        producer.send(new ProducerRecord<>("test", "test1"), new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                log.info("发送成功");
                latch.countDown();
            }
        });


        latch.await(5, TimeUnit.SECONDS);
        Thread.sleep(111111111);
        log.info("程序结束");
    }
}
