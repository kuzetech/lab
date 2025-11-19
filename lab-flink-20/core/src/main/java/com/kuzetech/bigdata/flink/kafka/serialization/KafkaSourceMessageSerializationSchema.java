package com.kuzetech.bigdata.flink.kafka.serialization;

import com.kuzetech.bigdata.flink.kafka.domain.KafkaSourceMessage;
import lombok.AllArgsConstructor;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@AllArgsConstructor
public class KafkaSourceMessageSerializationSchema
        implements KafkaRecordSerializationSchema<KafkaSourceMessage> {

    private final String sinkTopic;

    @Override
    public void open(SerializationSchema.InitializationContext context, KafkaSinkContext sinkContext) throws Exception {
        KafkaRecordSerializationSchema.super.open(context, sinkContext);
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(KafkaSourceMessage data, KafkaSinkContext context, Long timestamp) {
        if (data.getKey() == null && data.getProperties() == null) {
            return new ProducerRecord<>(
                    sinkTopic,
                    data.getData());
        }

        RecordHeaders headers = null;
        Map<String, String> properties = data.getProperties();
        if (properties != null) {
            properties.entrySet().removeIf(entry -> entry.getValue() == null);
            if (!properties.isEmpty()) {
                headers = new RecordHeaders();
                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    headers.add(new RecordHeader(
                            entry.getKey(),
                            entry.getValue().getBytes(StandardCharsets.UTF_8)
                    ));
                }
            }
        }

        return new ProducerRecord<>(
                sinkTopic,
                null,
                data.getKey(),
                data.getData(),
                headers);
    }
}
