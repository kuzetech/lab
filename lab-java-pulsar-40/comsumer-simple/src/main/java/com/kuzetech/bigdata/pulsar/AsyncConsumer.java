package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ConsumerUtil;
import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.concurrent.CompletableFuture;

public class AsyncConsumer {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Consumer<byte[]> consumer = ConsumerUtil.getCommonConsumer(client, "sink-topic")
        ) {
            CompletableFuture<Message<byte[]>> messageFuture = consumer.receiveAsync();
            messageFuture.thenAccept((Message<byte[]> msg) -> {
                System.out.println("Message received: " + new String(msg.getData()));
                try {
                    consumer.acknowledge(msg);
                } catch (PulsarClientException e) {
                    consumer.negativeAcknowledge(msg);
                }
            });
        }
    }
}
