package com.kuzetech.bigdata.flink.kafka.serialization;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzetech.bigdata.flink.funny.FunnyMessage;
import com.kuzetech.bigdata.flink.json.ObjectMapperInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

@Slf4j
public class KafkaFunnyMessageDeserializationSchema implements KafkaRecordDeserializationSchema<FunnyMessage> {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperInstance.getInstance();

    @Override
    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<FunnyMessage> out) throws IOException {
        JsonNode jsonNode = OBJECT_MAPPER.readTree(record.value());
        JsonNode dataNode = jsonNode.get("data");
        String app = jsonNode.get("app").asText();
        String event = dataNode.get("#event").asText();
        String logId = dataNode.get("#log_id").asText();
        long ingestTime = dataNode.get("#ingest_time").asLong();
        out.collect(new FunnyMessage(FunnyMessage.CHANNEL_KAFKA, app, event, logId, ingestTime));
    }

    @Override
    public TypeInformation<FunnyMessage> getProducedType() {
        return Types.POJO(FunnyMessage.class);
    }

}
