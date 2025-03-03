package com.kuzetech.bigdata.pulsar.util;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Reader;

public class ReaderUtil {

    public static Reader<byte[]> getCommonReader(PulsarClient client, String topic) throws PulsarClientException {
        return client.newReader()
                .topic("public/default/" + topic)
                .startMessageId(MessageId.earliest)
                .create();
    }
}
