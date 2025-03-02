package com.kuzetech.bigdata.pulsar.producer;

import com.kuzetech.bigdata.pulsar.PulsarUtil;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.concurrent.CompletableFuture;

public class AsyncProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Producer<byte[]> producer = PulsarUtil.getCommonProducer(client, "source-topic");
        ) {
            CompletableFuture<MessageId> future = producer.sendAsync("hello".getBytes());
        }
    }
}
