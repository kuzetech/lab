package com.kuzetech.bigdata.pulsar;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class BaseConsumer {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = PulsarClient.builder()
                        .serviceUrl("pulsar://localhost:6650")
                        .build();

                Consumer<byte[]> consumer = client.newConsumer()
                        .topic("public/default/sink-topic")
                        .subscriptionName("my-subscription")
                        .subscribe();

        ) {
            while (true) {
                Message<byte[]> msg = consumer.receive();
                try {
                    System.out.println("Message received: " + new String(msg.getData()));
                    consumer.acknowledge(msg);
                } catch (Exception e) {
                    consumer.negativeAcknowledge(msg);
                    break;
                }
            }
        }
    }
}
