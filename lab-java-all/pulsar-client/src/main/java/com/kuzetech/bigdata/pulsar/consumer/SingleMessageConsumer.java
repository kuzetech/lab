package com.kuzetech.bigdata.pulsar.consumer;

import com.kuzetech.bigdata.pulsar.util.ConsumerUtil;
import com.kuzetech.bigdata.pulsar.util.PulsarUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class SingleMessageConsumer {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Consumer<byte[]> consumer = ConsumerUtil.getSimpleConsumer(client, "async-topic")
        ) {
            Message<byte[]> msg = consumer.receive();
            try {
                System.out.println("Message received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        }
    }
}
