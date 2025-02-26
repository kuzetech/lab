package com.kuzetech.bigdata.pulsar;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class PulsarUtil {

    public static PulsarClient getCommonPulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650")
                .listenerName("external")
                .build();
    }

    public static Consumer<byte[]> getCommonConsumer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newConsumer()
                .topic("public/default/" + topic)
                .subscriptionName("client-subscription")
                .subscribe();
    }

    public static Producer<byte[]> getCommonProducer(PulsarClient client, String topic) throws PulsarClientException {
        return client.newProducer()
                .topic("public/default/" + topic)
                .create();
    }
}
