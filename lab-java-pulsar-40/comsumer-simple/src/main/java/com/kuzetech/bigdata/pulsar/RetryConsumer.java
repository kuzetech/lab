package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ConsumerUtil;
import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.concurrent.TimeUnit;

public class RetryConsumer {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Consumer<byte[]> consumer = ConsumerUtil.getRetryConsumer(client, "sink-topic")
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
