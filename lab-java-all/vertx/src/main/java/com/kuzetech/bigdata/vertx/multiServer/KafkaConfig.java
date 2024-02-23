package com.kuzetech.bigdata.vertx.multiServer;

import io.vertx.core.shareddata.Shareable;

import java.util.Properties;

public class KafkaConfig implements Shareable {

    private Properties properties;

    public KafkaConfig(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
