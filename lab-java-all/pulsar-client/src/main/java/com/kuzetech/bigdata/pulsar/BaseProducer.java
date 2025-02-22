package com.kuzetech.bigdata.pulsar;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class BaseProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        try (
                PulsarClient client = PulsarClient.builder()
                        .serviceUrl("pulsar://localhost:6650")
                        .build();

                Producer<byte[]> producer = client.newProducer()
                        .topic("public/default/part-topic")
                        .create();
        ) {
            for (int i = 0; i < 100; i++) {
                producer.send(String.valueOf(i).getBytes());
                Thread.sleep(1000);
            }
        }
    }
}
