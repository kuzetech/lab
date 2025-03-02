package com.kuzetech.bigdata.pulsar.consumer;

import com.kuzetech.bigdata.pulsar.PulsarUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class BaseConsumer {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Consumer<byte[]> consumer = PulsarUtil.getCommonConsumer(client, "sink-topic")
        ) {
            while (true) {
                /*
                * 当生产者中的分批发送是将多条消息打包为一条消息发送到服务端
                * 消费者会接受批消息，然后拆解成单条消息
                * 每次消费都是从内存队列中取出一条消息
                * */
                Message<byte[]> msg = consumer.receive();
                try {
                    System.out.println("Message received: " + new String(msg.getData()));
                    consumer.acknowledge(msg);
                } catch (Exception e) {
                    consumer.negativeAcknowledge(msg);
                    break;
                }
            }
        }
    }
}
