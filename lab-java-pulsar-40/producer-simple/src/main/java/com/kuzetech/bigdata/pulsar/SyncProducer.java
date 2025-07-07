package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class SyncProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Producer<String> producer = ProducerUtil.getSimpleProducer(client, "source");
        ) {
            for (int i = 0; i < 100; i++) {
                producer.send(String.valueOf(i));
                Thread.sleep(1000);
            }
        }
    }
}
