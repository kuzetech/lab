package com.kuzetech.bigdata.pulsar.consumer;

import com.kuzetech.bigdata.pulsar.PulsarUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

// 用来监听消费者状态变动的监听器，可在故障转移模式下发生分区分配策略变化时监听状态的变动。
// becameActive在当前消费者获取到一个分区的消费权利时被调用，
// becameInactive在当前消费者没有分区消费权利时被调用
// 案例需要进行补充
public class ListenerEventConsumer {
    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Consumer<byte[]> consumer = PulsarUtil.getCommonConsumer(client, "sink-topic")
        ) {

        }
    }
}
