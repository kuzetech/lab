package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsyncProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException, ExecutionException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Producer<String> producer = ProducerUtil.getSimpleProducer(client, "source");
        ) {
            CompletableFuture<MessageId>[] futures = new CompletableFuture[5];

            for (int i = 0; i < 5; i++) {
                CompletableFuture<MessageId> future = producer.sendAsync(String.valueOf(i));
                futures[i] = (future);
            }

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures);
            allOf.get();
            System.out.println("send success");
        }
    }
}
