package com.kuzetech.bigdata.flink.pulsar.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzetech.bigdata.flink.funny.FunnyMessage;
import com.kuzetech.bigdata.flink.json.ObjectMapperInstance;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.pulsar.source.reader.deserializer.PulsarDeserializationSchema;
import org.apache.flink.util.Collector;
import org.apache.pulsar.client.api.Message;

public class PulsarFunnyMessageDeserializationSchema implements PulsarDeserializationSchema<FunnyMessage> {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperInstance.getInstance();

    @Override
    public void deserialize(Message<byte[]> message, Collector<FunnyMessage> out) throws Exception {
        JsonNode jsonNode = OBJECT_MAPPER.readTree(message.getData());
        JsonNode dataNode = jsonNode.get("data");
        String app = jsonNode.get("app").asText();
        String event = dataNode.get("#event").asText();
        String logId = dataNode.get("#log_id").asText();
        long ingestTime = dataNode.get("#ingest_time").asLong();
        out.collect(new FunnyMessage(FunnyMessage.CHANNEL_PULSAR, app, event, logId, ingestTime));
    }

    @Override
    public TypeInformation<FunnyMessage> getProducedType() {
        return TypeInformation.of(FunnyMessage.class);
    }
}
