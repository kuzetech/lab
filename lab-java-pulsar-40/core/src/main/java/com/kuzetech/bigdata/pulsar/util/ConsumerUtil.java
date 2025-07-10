package com.kuzetech.bigdata.pulsar.util;

import org.apache.pulsar.client.api.*;

import java.util.concurrent.TimeUnit;

public class ConsumerUtil {

    public static Consumer<byte[]> getSimpleConsumer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newConsumer()
                .subscriptionName("lab-consumer-simple")
                .topic(TopicUtil.getDefaultCompleteTopic(topic))
                .subscriptionType(SubscriptionType.Exclusive) // 定义订阅模式，订阅模式分为独占、故障转移、共享、键共享，注意默认为独占
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscribe();
    }

    public static Consumer<byte[]> getIngestConsumer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newConsumer()
                .subscriptionName("lab-consumer-ingest")
                .topic(TopicUtil.getDefaultCompleteTopic(topic))
                .subscriptionType(SubscriptionType.Exclusive) // 定义订阅模式，订阅模式分为独占、故障转移、共享、键共享，注意默认为独占
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscribe();
    }

    public static Consumer<byte[]> getSimpleBatchIndexAcknowledgmentConsumer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newConsumer()
                .subscriptionName("lab-consumer-simple-batch")
                .topic(TopicUtil.getDefaultCompleteTopic(topic))
                .subscriptionType(SubscriptionType.Exclusive) // 定义订阅模式，订阅模式分为独占、故障转移、共享、键共享，注意默认为独占
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .enableBatchIndexAcknowledgment(true)
                .subscribe();
    }

    public static Consumer<byte[]> getCommonConsumer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newConsumer()
                .subscriptionName("client-subscription")
                .consumerName("lab-consumer")
                .topic(TopicUtil.getDefaultCompleteTopic(topic))
                //.topics() // 可以指定一堆 topic
                //.topicsPattern("persistent:// public/ default/ pattern-topic-.*") // 按照正则表达式的规则匹配一组主题
                //.patternAutoDiscoveryPeriod(1, TimeUnit.MINUTES) // 和topicsPattern一起使用，表示每隔多长时间重新按照模式匹配主题
                //.subscriptionTopicsMode(RegexSubscriptionMode.PersistentOnly) // 使用正则表达式订阅主题时，你可以选择订阅哪种类型的主题
                .subscriptionType(SubscriptionType.Exclusive) // 定义订阅模式，订阅模式分为独占、故障转移、共享、键共享，注意默认为独占
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                // .subscriptionMode(SubscriptionMode.Durable) // 需要补充作用
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
                // 消息确认超时机制，似乎无法控制重发消息的时间
                .ackTimeout(0, TimeUnit.SECONDS) // 消息未确认的超时时间，设置为 0 默认关闭
                .ackTimeoutTickTime(1, TimeUnit.SECONDS) // 推测是检查消息确认超时的频率
                // 否定确认重试机制，比起超时机制，否认确认可以更精确地控制单个消息的重新发送时间，并且可以避免在超时机制中，因数据处理较慢超过超时阈值而引起重新发送无效的情况
                .negativeAckRedeliveryDelay(1, TimeUnit.MINUTES) // 当应用程序使用negativeAcknowledge方法时，失败的消息会在该时间后重新发送
                // 压实机制，注意不等于压缩
                .readCompacted(false) // 如果启用，消费者会从压实的主题中读取消息，而不是读取主题的完整消息积压。消费者只能看到压缩主题中每个键的最新值
                .enableRetry(false) // 消费者级别的自动重试
                // 用于启动消费者的死信机制
                .deadLetterPolicy(DeadLetterPolicy.builder() // 默认情况下，某些消息可能会多次重新发送，甚至可能永远都在重试中
                        .maxRedeliverCount(10) // 消息具有最大重新发送计数
                        //.retryLetterTopic() // 失败的消息会被发送到的重试队列
                        .deadLetterTopic("{TopicName}-{Subscription}-DLQ") // 失败的消息会被发送到的死信队列
                        //.initialSubscriptionName() // 死信队列订阅名，不设置的话订阅将不会被创建，并且 broker 需要允许 allowAutoSubscriptionCreation 否则创建失败
                        .build())
                .subscribe();
    }

    public static Consumer<byte[]> getRetryConsumer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newConsumer()
                .subscriptionName("client-subscription")
                .topic(TopicUtil.getDefaultCompleteTopic(topic))
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                .enableRetry(true)
                .subscribe();
    }

    public static Consumer<byte[]> getBatchIndexAcknowledgmentConsumer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newConsumer()
                .subscriptionName("client-subscription")
                .topic(TopicUtil.getDefaultCompleteTopic(topic))
                .subscriptionInitialPosition(SubscriptionInitialPosition.Latest)
                // ensure batch index acknowledgment is enabled on the broker side
                .enableBatchIndexAcknowledgment(true)
                .subscribe();
    }
}
