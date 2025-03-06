package com.kuzetech.bigdata.pulsar.producer;

import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import com.kuzetech.bigdata.pulsar.util.PulsarUtil;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class SimpleProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Producer<byte[]> producer = ProducerUtil.getSimpleProducer(client, "source");
        ) {
            for (int i = 0; i < 100; i++) {
                producer.send(String.valueOf(i).getBytes());
                Thread.sleep(1000);
            }
        }
    }
}
