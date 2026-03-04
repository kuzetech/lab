package com.kuzetech.bigdata.pulsar.eos;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;

public class Main {
    public static void main(String[] args) throws Exception {
        // Pulsar broker 连接地址
        String brokerUrl = "pulsar://localhost:6650";
        String topic = "persistent://public/default/test-topic";
        String subscriptionName = "test-subscription";

        // 创建 Pulsar 客户端
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(brokerUrl)
                .build();

        try (client; Consumer<byte[]> consumer = client.newConsumer()
                .topic(topic)
                .subscriptionName(subscriptionName)
                .subscriptionType(SubscriptionType.Exclusive)
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .enableBatchIndexAcknowledgment(true)
                .subscribe()) {

            System.out.println("事务消费者已启动，订阅主题: " + topic);
            System.out.println("订阅名称: " + subscriptionName);

            // 消费消息
            int messageCount = 0;
            while (messageCount < 10) {
                try {
                    // 接收消息，超时时间为 5 秒
                    Message<byte[]> message = consumer.receive(5000, java.util.concurrent.TimeUnit.MILLISECONDS);

                    if (message != null) {
                        // 获取消息内容
                        byte[] data = message.getValue();
                        String messageContent = new String(data);

                        System.out.println("收到消息 [" + (messageCount + 1) + "]: " + messageContent);
                        System.out.println("  消息 ID: " + message.getMessageId());
                        System.out.println("  主题: " + message.getTopicName());
                        System.out.println("  时间戳: " + message.getEventTime());

                        // 确认消息
                        consumer.acknowledge(message);
                        messageCount++;
                    } else {
                        System.out.println("等待消息超时...");
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("消费消息时出错: " + e.getMessage());
                    break;
                }
            }

            System.out.println("共消费 " + messageCount + " 条消息");
        } finally {
            System.out.println("Pulsar 客户端已关闭");
        }
    }
}