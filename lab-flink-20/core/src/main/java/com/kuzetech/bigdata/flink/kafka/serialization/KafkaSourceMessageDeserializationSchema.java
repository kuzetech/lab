package com.kuzetech.bigdata.flink.kafka.serialization;


import com.kuzetech.bigdata.flink.kafka.domain.KafkaSourceMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

@Slf4j
public class KafkaSourceMessageDeserializationSchema implements KafkaRecordDeserializationSchema<KafkaSourceMessage> {

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<KafkaSourceMessage> out) throws IOException {
        out.collect(new KafkaSourceMessage(record));
    }

    @Override
    public TypeInformation<KafkaSourceMessage> getProducedType() {
        return Types.POJO(KafkaSourceMessage.class);
    }

}
