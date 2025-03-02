package com.kuzetech.bigdata.pulsar.producer;

import com.kuzetech.bigdata.pulsar.PulsarUtil;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class BaseProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Producer<byte[]> producer = PulsarUtil.getCommonProducer(client, "source-topic");
        ) {
            for (int i = 0; i < 100; i++) {
                producer.send(String.valueOf(i).getBytes());
                Thread.sleep(1000);
            }
        }
    }
}
