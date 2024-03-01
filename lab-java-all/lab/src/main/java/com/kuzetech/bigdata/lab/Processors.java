package com.kuzetech.bigdata.lab;

import com.fasterxml.jackson.databind.node.ObjectNode;

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
}
