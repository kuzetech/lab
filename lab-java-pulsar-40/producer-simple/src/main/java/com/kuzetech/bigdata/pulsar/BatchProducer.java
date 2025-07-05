package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import com.kuzetech.bigdata.pulsar.util.PulsarUtil;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class BatchProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException, ExecutionException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Producer<byte[]> producer = ProducerUtil.get10BatchProducer(client, "funnydb-ingest-receive");
        ) {
            int count = 0;
            for (int i = 1; i <= 20; i++) {
                producer.newMessage()
                        .value(String.valueOf(i).getBytes(StandardCharsets.UTF_8))
                        .sendAsync();
                count++;
                if (count % 10 == 0) {
                    count = 0;
                    System.out.println("sent messages batch");
                    Thread.sleep(3000);
                }
            }

            Thread.sleep(3000);
            System.out.println("sent messages end");
        }
    }
}
