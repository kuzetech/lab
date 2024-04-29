package com.kuze.bigdata.kstreams.funnypipe;

import org.apache.kafka.common.serialization.Serializer;

public class DestMessageSerializer implements Serializer<DestMessage> {
    @Override
    public byte[] serialize(String topic, DestMessage data) {
        return data.getValue();
    }
}
