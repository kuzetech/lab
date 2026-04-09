package com.funnydb.mutation;

import com.funnydb.mutation.config.MutationAppConfig;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class MutationAppConfigTests {

    @Test
    public void shouldLoadConfigWithDefaults() {
        Properties properties = new Properties();
        properties.setProperty("kafka.bootstrap.servers", "localhost:9092");
        properties.setProperty("kafka.consumer.group-id", "mutation-group");
        properties.setProperty("kafka.producer.transactional-id-prefix", "mutation-tx");
        properties.setProperty("redis.address", "redis://localhost:6379");

        MutationAppConfig config = MutationAppConfig.fromProperties(properties);

        assertEquals("localhost:9092", config.getKafkaBootstrapServers());
        assertEquals("mutation-group", config.getKafkaConsumerGroupId());
        assertEquals("funnydb-ingest-mutation-events", config.getKafkaConsumerInputTopic());
        assertEquals("mutation-tx", config.getKafkaProducerTransactionalIdPrefix());
        assertEquals("redis://localhost:6379", config.getRedisAddress());
        assertEquals(200, config.getTxBatchSize());
        assertEquals(1000L, config.getTxFlushIntervalMs());
        assertEquals("0.0.0.0", config.getMetricsBindAddress());
        assertEquals(8080, config.getMetricsPort());
    }
}
