package com.xmfunny.funnydb.metadata;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

@Data
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
}
