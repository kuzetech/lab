package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ConsumerUtil;
import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class SizeMessageBatchIndexAckConsumer {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Consumer<byte[]> consumer = ConsumerUtil.getSimpleBatchIndexAcknowledgmentConsumer(client, "test2")
        ) {
            for (int i = 0; i < 2; i++) {
                Message<byte[]> msg = consumer.receive();
                System.out.println("Message received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            }
        }
    }
}
