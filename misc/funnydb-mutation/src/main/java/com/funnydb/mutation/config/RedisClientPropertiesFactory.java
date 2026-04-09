package com.funnydb.mutation.config;

import java.util.Properties;

public final class RedisClientPropertiesFactory {
    private RedisClientPropertiesFactory() {
    }

    public static Properties build(MutationAppConfig config) {
        Properties properties = new Properties();
        properties.setProperty("address", config.getRedisAddress());
        return properties;
    }
}
