package com.kuzetech.bigdata.pulsar.util;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class PulsarUtil {

    public static PulsarClient getCommonPulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650")
                .ioThreads(Runtime.getRuntime().availableProcessors())
                .listenerThreads(Runtime.getRuntime().availableProcessors())
                .listenerName("external")
                .build();
    }
}
