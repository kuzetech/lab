package com.kuzetech.bigdata.pulsar.util;

import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.concurrent.TimeUnit;

public class ProducerUtil {

    public static Producer<byte[]> getCommonProducer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newProducer()
                .producerName("lab-producer")
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

    public static Producer<byte[]> getAsyncProducer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newProducer()
                // 默认开启
                //.enableBatching(true)
                .producerName("lab-producer-async")
                .batchingMaxPublishDelay(100, TimeUnit.MILLISECONDS)
                .topic("public/default/" + topic)
                .create();
    }
}
