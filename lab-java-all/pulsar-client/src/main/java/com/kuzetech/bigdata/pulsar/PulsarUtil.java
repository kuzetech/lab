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
                .consumerName("lab-consumer")
                .topic("public/default/" + topic)
                //.topics() // 可以指定一堆 topic
                //.topicsPattern("persistent:// public/ default/ pattern-topic-.*") // 按照正则表达式的规则匹配一组主题
                //.patternAutoDiscoveryPeriod(1, TimeUnit.MINUTES) // 和topicsPattern一起使用，表示每隔多长时间重新按照模式匹配主题
                //.subscriptionTopicsMode(RegexSubscriptionMode.PersistentOnly) // 使用正则表达式订阅主题时，你可以选择订阅哪种类型的主题
                .subscriptionType(SubscriptionType.Exclusive) // 定义订阅模式，订阅模式分为独占、故障转移、共享、键共享，注意默认为独占
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                // .subscriptionMode(SubscriptionMode.Durable) // 需要补充作用
                .subscriptionName("client-subscription")
                // .priorityLevel(0) // 订阅优先级。共享模式下有效。服务端仅在最高优先级消费者不能接收消息时，才发送给下一级
                .receiverQueueSize(1000) // 设置消费者接收队列的大小，在应用程序调用Receive方法之前，消费者会在内存中缓存部分消息。该参数用于控制队列中最多缓存的消息数。配置高于默认值的值虽然会提高使用者的吞吐量，但会占用更多的内存。并且当达到限制时，所有接收队列都不能再继续接收数据了。
                .maxTotalReceiverQueueSizeAcrossPartitions(50000) // 设置多个分区总的最大内存队列缓存长度
                .batchReceivePolicy(BatchReceivePolicy.builder() // 使用 batchReceive 才会触发规则
                        // 满足其中一个条件便返回
                        .maxNumMessages(-1)
                        .maxNumBytes(10 * 1024 * 1024)
                        .timeout(100, TimeUnit.MILLISECONDS)
                        .messagesFromMultiTopicsEnabled(false)
                        .build())
                // 消费者自动确认机制
                .acknowledgmentGroupTime(100, TimeUnit.MILLISECONDS)
                .maxAcknowledgmentGroupSize(1000)
                .ackTimeout(0, TimeUnit.SECONDS) // 消息未确认的超时时间，设置为 0 默认关闭。
                .subscribe();
    }

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
}
