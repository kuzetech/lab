package com.kuzetech.bigdata.pulsar.util;

import org.apache.pulsar.client.api.*;

import java.util.concurrent.TimeUnit;

public class ProducerUtil {

    public static Producer<String> getSimpleProducer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newProducer(Schema.STRING)
                .producerName("lab-producer-simple")
                .topic(TopicUtil.getDefaultCompleteTopic(topic))
                .create();
    }

    public static Producer<byte[]> getIngestProducer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newProducer(Schema.BYTES)
                .producerName("lab-producer-ingest")
                .topic(TopicUtil.getDefaultCompleteTopic(topic))
                .create();
    }

    public static Producer<String> getSimpleBatchProducer(PulsarClient client, String topic, Integer batchSize) throws PulsarClientException {
        return client.newProducer(Schema.STRING)
                .producerName("lab-producer-batch")
                .batchingMaxPublishDelay(24, TimeUnit.HOURS)
                .batchingMaxBytes(1024 * 1024 * 1024) // 1GB
                .batchingMaxMessages(batchSize)
                .topic(TopicUtil.getDefaultCompleteTopic(topic))
                .create();
    }

    public static Producer<byte[]> getCommonProducer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newProducer()
                .producerName("lab-producer-common")
                .topic(TopicUtil.getDefaultCompleteTopic(topic))
                .enableBatching(true) // 默认开启，仅对 sendAsync 有效
                .sendTimeout(30, TimeUnit.SECONDS)
                .maxPendingMessages(0)
                .blockIfQueueFull(false)
                .batchingMaxPublishDelay(1, TimeUnit.MILLISECONDS)
                .batchingMaxMessages(1000)
                .batchingMaxBytes(128 * 1024) // 128KB
                .compressionType(CompressionType.NONE)
                .accessMode(ProducerAccessMode.Shared)
                .create();
    }
}
