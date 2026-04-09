package com.funnydb.mutation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class MutationAppConfig {
    private final String kafkaBootstrapServers;
    private final String kafkaConsumerGroupId;
    private final String kafkaConsumerInputTopic;
    private final String kafkaProducerTransactionalIdPrefix;
    private final String redisAddress;
    private final int workerThreadCount;
    private final int txBatchSize;
    private final long txFlushIntervalMs;
    private final String metricsBindAddress;
    private final int metricsPort;

    public MutationAppConfig(String kafkaBootstrapServers,
                             String kafkaConsumerGroupId,
                             String kafkaConsumerInputTopic,
                             String kafkaProducerTransactionalIdPrefix,
                             String redisAddress,
                             int workerThreadCount,
                             int txBatchSize,
                             long txFlushIntervalMs,
                             String metricsBindAddress,
                             int metricsPort) {
        this.kafkaBootstrapServers = require(kafkaBootstrapServers, "kafka.bootstrap.servers");
        this.kafkaConsumerGroupId = require(kafkaConsumerGroupId, "kafka.consumer.group-id");
        this.kafkaConsumerInputTopic = require(kafkaConsumerInputTopic, "kafka.consumer.input-topic");
        this.kafkaProducerTransactionalIdPrefix = require(kafkaProducerTransactionalIdPrefix, "kafka.producer.transactional-id-prefix");
        this.redisAddress = require(redisAddress, "redis.address");
        this.workerThreadCount = workerThreadCount;
        this.txBatchSize = txBatchSize;
        this.txFlushIntervalMs = txFlushIntervalMs;
        this.metricsBindAddress = require(metricsBindAddress, "metrics.bind-address");
        this.metricsPort = metricsPort;
    }

    public MutationAppConfig(String kafkaBootstrapServers,
                             String kafkaConsumerGroupId,
                             String kafkaConsumerInputTopic,
                             String kafkaProducerTransactionalIdPrefix,
                             String redisAddress,
                             int workerThreadCount,
                             int txBatchSize,
                             long txFlushIntervalMs) {
        this(
                kafkaBootstrapServers,
                kafkaConsumerGroupId,
                kafkaConsumerInputTopic,
                kafkaProducerTransactionalIdPrefix,
                redisAddress,
                workerThreadCount,
                txBatchSize,
                txFlushIntervalMs,
                "0.0.0.0",
                8080
        );
    }

    public static MutationAppConfig fromProperties(Properties properties) {
        int defaultThreads = Math.max(1, Runtime.getRuntime().availableProcessors() * 2);
        return new MutationAppConfig(
                properties.getProperty("kafka.bootstrap.servers"),
                properties.getProperty("kafka.consumer.group-id"),
                properties.getProperty("kafka.consumer.input-topic", "funnydb-ingest-mutation-events"),
                properties.getProperty("kafka.producer.transactional-id-prefix"),
                properties.getProperty("redis.address"),
                Integer.parseInt(properties.getProperty("worker.thread-count", String.valueOf(defaultThreads))),
                Integer.parseInt(properties.getProperty("tx.batch-size", "200")),
                Long.parseLong(properties.getProperty("tx.flush-interval-ms", "1000")),
                properties.getProperty("metrics.bind-address", "0.0.0.0"),
                Integer.parseInt(properties.getProperty("metrics.port", "8080"))
        );
    }

    public static MutationAppConfig load(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        return fromProperties(properties);
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public String getKafkaConsumerGroupId() {
        return kafkaConsumerGroupId;
    }

    public String getKafkaConsumerInputTopic() {
        return kafkaConsumerInputTopic;
    }

    public String getKafkaProducerTransactionalIdPrefix() {
        return kafkaProducerTransactionalIdPrefix;
    }

    public String getRedisAddress() {
        return redisAddress;
    }

    public int getWorkerThreadCount() {
        return workerThreadCount;
    }

    public int getTxBatchSize() {
        return txBatchSize;
    }

    public long getTxFlushIntervalMs() {
        return txFlushIntervalMs;
    }

    public String getMetricsBindAddress() {
        return metricsBindAddress;
    }

    public int getMetricsPort() {
        return metricsPort;
    }

    private static String require(String value, String key) {
        return Objects.requireNonNull(value, "Missing config: " + key);
    }
}
