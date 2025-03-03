package com.kuzetech.bigdata.pulsar.consumer;

import com.kuzetech.bigdata.pulsar.PulsarUtil;
import org.apache.pulsar.client.api.*;

public class BatchConsumer {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Consumer<byte[]> consumer = PulsarUtil.getCommonConsumer(client, "sink-topic")
        ) {
            while (true) {
                /*
                 * 这里的batchReceive和生产者中的分批发送并无直接关系，生产者中的分批发送是将多条消息打包为一条消息发送到服务端。消费者在消费消息时并无感知。
                 * 而这里提到的批量接收消息是指消息到达消费者之后的消息接收方式，可以理解为消费者对单条接收的封装。
                 * 之前是从内存队列中取出一条消息，现在是在内存队列中取出多条消息。
                 * 目前调整该参数不会显著加快服务端到消费者的传输速度
                 * */
                Messages<byte[]> messages = consumer.batchReceive();
                for (Message<byte[]> msg : messages) {
                    System.out.println("Message received: " + new String(msg.getData()));
                    // 可以选择确认单条消息
                    /*try {
                        consumer.acknowledge(msg);;
                    } catch (PulsarClientException e) {
                        consumer.negativeAcknowledge(msg);
                    }*/
                }
                // 或者确认整批消息
                try {
                    consumer.acknowledge(messages);
                } catch (PulsarClientException e) {
                    consumer.negativeAcknowledge(messages);
                    break;
                }
            }
        }
    }
}
