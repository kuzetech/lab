package com.kuzetech.bigdata.flink19;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class KafkaEventDeserializationSchema implements KafkaRecordDeserializationSchema<InputMessage> {

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<InputMessage> out) {
        out.collect(InputMessage.createFromRecord(record));
    }

    @Override
    public TypeInformation<InputMessage> getProducedType() {
        return TypeInformation.of(InputMessage.class);
    }

}
