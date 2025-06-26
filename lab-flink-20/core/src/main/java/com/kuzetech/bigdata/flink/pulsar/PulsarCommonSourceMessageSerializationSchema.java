package com.kuzetech.bigdata.flink.pulsar;

import com.kuzetech.bigdata.flink.base.CommonSourceMessage;
import org.apache.flink.connector.pulsar.sink.writer.context.PulsarSinkContext;
import org.apache.flink.connector.pulsar.sink.writer.message.PulsarMessage;
import org.apache.flink.connector.pulsar.sink.writer.message.PulsarMessageBuilder;
import org.apache.flink.connector.pulsar.sink.writer.serializer.PulsarSerializationSchema;

import java.util.Map;

public class PulsarCommonSourceMessageSerializationSchema implements PulsarSerializationSchema<CommonSourceMessage> {
    @Override
    public PulsarMessage<byte[]> serialize(CommonSourceMessage input, PulsarSinkContext sinkContext) {
        PulsarMessageBuilder<byte[]> builder = PulsarMessage.builder(input.getData());
        if (input.getKey() != null) {
            builder.keyBytes(input.getKey());
        }
        Map<String, String> properties = input.getProperties();
        if (properties != null) {
            // 移除 value==null 的元素
            properties.entrySet().removeIf(entry -> entry.getValue() == null);
            if (!properties.isEmpty()) {
                builder.properties(properties);
            }
        }
        return builder.build();
    }
}