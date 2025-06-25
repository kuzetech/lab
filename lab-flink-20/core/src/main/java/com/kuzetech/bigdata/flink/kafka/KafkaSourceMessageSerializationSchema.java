package com.kuzetech.bigdata.flink.kafka;

import lombok.AllArgsConstructor;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

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
        if (data.getKey() != null) {
            return new ProducerRecord<>(
                    sinkTopic,
                    data.getKey(),
                    data.getData());
        } else {
            return new ProducerRecord<>(
                    sinkTopic,
                    data.getData());
        }
    }
}
