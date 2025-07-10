package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import com.kuzetech.bigdata.pulsar.util.ConsumerUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class TrackEventConsumer {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Consumer<byte[]> consumer = ConsumerUtil.getIngestConsumer(client, "funnydb-flink-track-events")
        ) {
            Message<byte[]> msg = consumer.receive();
            System.out.println("Message received: " + new String(msg.getData()));
        }
    }
}
