package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class TestLoadBalanceProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException, ExecutionException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Producer<byte[]> producer = ProducerUtil.get10BatchProducer(client, "test8");
        ) {
            Random random = new Random();
            for (int i = 1; i <= 1000; i++) {
                producer.newMessage()
                        .key(String.valueOf(i))
                        .value(String.valueOf(i).getBytes(StandardCharsets.UTF_8))
                        .sendAsync();
                Thread.sleep(random.nextInt(30));
            }
            producer.flush();
            System.out.println("sent messages end");
        }
    }
}
