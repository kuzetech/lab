package com.kuzetech.bigdata.pulsar.producer;

import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import com.kuzetech.bigdata.pulsar.util.PulsarUtil;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BatchProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException, ExecutionException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Producer<byte[]> producer = ProducerUtil.getFiveSecondBatchProducer(client, "test2");
        ) {
            List<CompletableFuture<MessageId>> futures = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                CompletableFuture<MessageId> future = producer.newMessage()
                        .value(String.valueOf(i).getBytes(StandardCharsets.UTF_8))
                        .sendAsync();
                futures.add(future);
            }
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
            allOf.get();
            System.out.println("successfully sent messages");
        }
    }
}
