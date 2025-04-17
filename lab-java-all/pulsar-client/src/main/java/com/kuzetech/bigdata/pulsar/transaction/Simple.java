package com.kuzetech.bigdata.pulsar.transaction;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.transaction.Transaction;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Simple {
    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        PulsarClient client = PulsarClient.builder()
                .enableTransaction(true)
                .serviceUrl("pulsar://localhost:6650")
                .build();

        Producer<String> producer = client.newProducer(Schema.STRING)
                .producerName("simple-transaction-producer")
                .topic("public/default/test")
                .sendTimeout(0, TimeUnit.SECONDS)
                .create();

        for (int i = 1; i <= 300; i++) {
            Transaction txn = null;
            try {
                txn = client.newTransaction()
                        .withTransactionTimeout(10, TimeUnit.SECONDS)
                        .build()
                        .get();

                producer.newMessage(txn).value("Hello Pulsar! outputTopicOne count : " + i).send();

                txn.commit().get();
            } catch (ExecutionException e) {
                if (txn != null) {
                    txn.abort();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Thread.sleep(1000);
        }

        log.info("数据发送完毕");
        producer.flush();
        producer.close();
        client.close();
        log.info("程序结束");
    }
}
