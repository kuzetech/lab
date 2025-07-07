package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class BatchProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Producer<String> producer = ProducerUtil.getSimpleBatchProducer(
                        client,
                        "funnydb-ingest-receive",
                        10);
        ) {
            for (int i = 1; i <= 20; i++) {
                producer.newMessage()
                        .value(String.valueOf(i))
                        .sendAsync();
            }

            Thread.sleep(3000);
            System.out.println("sent messages end");
        }
    }
}
