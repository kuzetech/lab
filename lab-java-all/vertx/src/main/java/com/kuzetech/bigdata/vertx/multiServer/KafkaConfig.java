package com.kuzetech.bigdata.vertx.multiServer;

import io.vertx.core.shareddata.Shareable;

import java.util.Map;

public class KafkaConfig implements Shareable {

    private Map<String, String> producerConfig;

    public KafkaConfig(Map<String, String> producerConfig) {
        this.producerConfig = producerConfig;
    }

    public Map<String, String> getProducerConfig() {
        return producerConfig;
    }

    public void setProducerConfig(Map<String, String> producerConfig) {
        this.producerConfig = producerConfig;
    }
}
