package com.kuzetech.bigdata.flink.pulsar;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.pulsar.source.reader.deserializer.PulsarDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.pulsar.client.api.Message;

public class PulsarSourceMessageDeserializationSchema implements PulsarDeserializationSchema<PulsarSourceMessage> {

    @Override
    public void deserialize(Message<byte[]> message, Collector<PulsarSourceMessage> out) throws Exception {
        out.collect(new PulsarSourceMessage(message));
    }

    @Override
    public TypeInformation<PulsarSourceMessage> getProducedType() {
        return TypeInformation.of(PulsarSourceMessage.class);
    }
}
