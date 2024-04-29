package com.kuze.bigdata.kstreams.funnypipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuze.bigdata.kstreams.funnypipe.utils.JsonUtil;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

public class EnrichedEventSerde implements Serde<EnrichedEvent> {

    @Override
    public Serializer<EnrichedEvent> serializer() {
        return new Serializer<EnrichedEvent>() {
            @Override
            public byte[] serialize(String topic, EnrichedEvent data) {
                try {
                    return JsonUtil.mapper.writeValueAsBytes(data);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public Deserializer<EnrichedEvent> deserializer() {
        return new Deserializer<EnrichedEvent>() {
            @Override
            public EnrichedEvent deserialize(String topic, byte[] data) {
                try {
                    return JsonUtil.mapper.readValue(data, EnrichedEvent.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public EnrichedEvent deserialize(String topic, Headers headers, byte[] data) {
                return new EnrichedEvent(headers, data);
            }
        };
    }
}
