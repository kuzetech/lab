package com.kuzetech.bigdata.pulsar.consumer;

import com.kuzetech.bigdata.pulsar.util.ConsumerUtil;
import com.kuzetech.bigdata.pulsar.util.PulsarUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

// 每条发向当前消费者的消息都会调用MessageListener.received方法
// 案例需要进行补充
public class ListenerMessageConsumer {
    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Consumer<byte[]> consumer = ConsumerUtil.getCommonConsumer(client, "sink-topic")
        ) {

        }
    }
}
