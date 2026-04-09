package com.funnydb.mutation;

import com.funnydb.mutation.config.KafkaClientPropertiesFactory;
import com.funnydb.mutation.config.MutationAppConfig;
import com.funnydb.mutation.config.RedisClientPropertiesFactory;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class ClientPropertiesFactoryTests {

    @Test
    public void shouldBuildKafkaConsumerAndProducerProperties() {
        MutationAppConfig config = new MutationAppConfig(
                "localhost:9092",
                "mutation-group",
                "funnydb-ingest-mutation-events",
                "mutation-tx",
                "redis://localhost:6379",
                4,
                200,
                1000L
        );

        Properties consumer = KafkaClientPropertiesFactory.buildConsumerProperties(config, "worker-1");
        Properties producer = KafkaClientPropertiesFactory.buildProducerProperties(config, "worker-1");

        assertEquals("false", consumer.getProperty("enable.auto.commit"));
        assertEquals("read_committed", consumer.getProperty("isolation.level"));
        assertEquals("all", producer.getProperty("acks"));
        assertEquals("true", producer.getProperty("enable.idempotence"));
        assertEquals("mutation-tx-worker-1", producer.getProperty("transactional.id"));
    }

    @Test
    public void shouldBuildRedisProperties() {
        MutationAppConfig config = new MutationAppConfig(
                "localhost:9092",
                "mutation-group",
                "funnydb-ingest-mutation-events",
                "mutation-tx",
                "redis://localhost:6379",
                4,
                200,
                1000L
        );

        Properties redisProperties = RedisClientPropertiesFactory.build(config);
        assertEquals("redis://localhost:6379", redisProperties.getProperty("address"));
    }
}
