package com.kuzetech.bigdata.flink18;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.pulsar.source.reader.deserializer.PulsarDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.pulsar.client.api.Message;

public class PulsarMessageDeserializationSchema implements PulsarDeserializationSchema<String> {

    @Override
    public void deserialize(Message<byte[]> message, Collector<String> out) throws Exception {
        out.collect("pkey-" + message.getKey());
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return TypeInformation.of(String.class);
    }
}
