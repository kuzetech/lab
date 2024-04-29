package com.kuze.bigdata.kstreams.funnypipe.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kuze.bigdata.kstreams.funnypipe.EnrichedEvent;
import com.kuze.bigdata.kstreams.funnypipe.utils.JsonUtil;

public class Processors {

    public static final String PROCESSOR_TYPE_SCHEMA_VALIDATOR_V2 = "SchemaValidatorV2";
    public static final String PROCESSOR_TYPE_EVENT_VALIDATOR_V2 = "EventValidatorV2";
    public static final String PROCESSOR_TYPE_MESSAGE_MODIFIER = "MessageModifier";
    public static final String PROCESSOR_TYPE_KAFKA_PRODUCER = "KafkaProducer";
    public static final String PROCESSOR_TYPE_CLIENT_IP_INJECTOR = "ClientIPInjector";
    public static final String PROCESSOR_TYPE_GEOIP = "GeoIP";
    public static final String PROCESSOR_TYPE_LIFECYCLE_INJECTOR = "LifeCycleInjector";

    private String type;
    private ObjectNode config;
    private Processors[] processors;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ObjectNode getConfig() {
        return config;
    }

    public void setConfig(ObjectNode config) {
        this.config = config;
    }

    public Processors[] getProcessors() {
        return processors;
    }

    public void setProcessors(Processors[] processors) {
        this.processors = processors;
    }

    public Processors getKafkaProcessor(String type, String event) throws JsonProcessingException {
        if (type.equalsIgnoreCase(EnrichedEvent.EVENT_TYPE_USER_MUTATION)) {
            return this.processors[0]
                    .getProcessors()[0]
                    .getProcessors()[0];
        } else if (type.equalsIgnoreCase(EnrichedEvent.EVENT_TYPE_DEVICE_MUTATION)) {
            return this.processors[0]
                    .getProcessors()[0]
                    .getProcessors()[0];
        } else {
            ObjectNode eventValidatorProcessorConfig = this.processors[0]
                    .getProcessors()[0].getConfig();
            JsonNode jsonNode = eventValidatorProcessorConfig.get("events").get(event).get("processors");

            Processors[] eventProcessors = JsonUtil.mapper.treeToValue(jsonNode, Processors[].class);

            return eventProcessors[0].getProcessors()[0].getProcessors()[0].getProcessors()[0];
        }
    }
}
