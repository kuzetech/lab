package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SequenceIdProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException, ExecutionException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Producer<String> producer = ProducerUtil.getSimpleProducer(client, "source");
        ) {
            producer.newMessage()
                    .value("test-sequence-id")
                    .send();
        }
    }
}
