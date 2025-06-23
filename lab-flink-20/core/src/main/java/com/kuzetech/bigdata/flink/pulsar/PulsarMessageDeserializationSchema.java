package com.kuzetech.bigdata.flink.pulsar;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.pulsar.source.reader.deserializer.PulsarDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.pulsar.client.api.Message;

public class PulsarMessageDeserializationSchema implements PulsarDeserializationSchema<PulsarMessage> {

    @Override
    public void deserialize(Message<byte[]> message, Collector<PulsarMessage> out) throws Exception {
        out.collect(new PulsarMessage(message));
    }

    @Override
    public TypeInformation<PulsarMessage> getProducedType() {
        return TypeInformation.of(PulsarMessage.class);
    }
}
