package com.kuzetech.bigdata.pulsar;

import org.apache.pulsar.client.api.*;

import java.util.concurrent.TimeUnit;

public class PulsarUtil {

    public static PulsarClient getCommonPulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650")
                .ioThreads(Runtime.getRuntime().availableProcessors())
                .listenerThreads(Runtime.getRuntime().availableProcessors())
                .listenerName("external")
                .build();
    }

    public static Consumer<byte[]> getCommonConsumer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newConsumer()
                .topic("public/default/" + topic)
                .subscriptionName("client-subscription")
                .subscribe();
    }

    public static Producer<byte[]> getCommonProducer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newProducer()
                .producerName("lab-client")
                .sendTimeout(30, TimeUnit.SECONDS)
                .maxPendingMessages(0)
                .blockIfQueueFull(false)
                .enableBatching(true) // 仅对 sendAsync 有效
                .batchingMaxPublishDelay(1, TimeUnit.MILLISECONDS)
                .batchingMaxMessages(1000)
                .batchingMaxBytes(128 * 1024) // 128KB
                .compressionType(CompressionType.NONE)
                .topic("public/default/" + topic)
                .create();
    }
}
