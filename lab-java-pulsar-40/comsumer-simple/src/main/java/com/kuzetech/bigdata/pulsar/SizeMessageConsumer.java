package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ConsumerUtil;
import com.kuzetech.bigdata.pulsar.util.PulsarUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class SizeMessageConsumer {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Consumer<byte[]> consumer = ConsumerUtil.getSimpleConsumer(client, "test")
        ) {
            for (int i = 0; i < 2; i++) {
                Message<byte[]> msg = consumer.receive();
                System.out.println("Message received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            }
        }
    }
}
