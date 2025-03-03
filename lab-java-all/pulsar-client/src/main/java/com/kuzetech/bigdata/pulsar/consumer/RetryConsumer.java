package com.kuzetech.bigdata.pulsar.consumer;

import com.kuzetech.bigdata.pulsar.PulsarUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.concurrent.TimeUnit;

public class RetryConsumer {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Consumer<byte[]> consumer = PulsarUtil.getRetryConsumer(client, "sink-topic")
        ) {
            for (int i = 0; i < 100; i++) {
                Message<byte[]> msg = consumer.receive();
                System.out.println("Message received: " + new String(msg.getData()));
                consumer.reconsumeLater(msg, 10, TimeUnit.SECONDS);
                System.out.println("Send retry message: " + new String(msg.getData()));
            }
        }
    }
}
