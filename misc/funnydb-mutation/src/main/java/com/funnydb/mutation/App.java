package com.funnydb.mutation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funnydb.mutation.config.KafkaClientPropertiesFactory;
import com.funnydb.mutation.config.MutationAppConfig;
import com.funnydb.mutation.infra.JedisRedisMutationStore;
import com.funnydb.mutation.infra.KafkaMutationConsumer;
import com.funnydb.mutation.infra.KafkaTransactionalSnapshotPublisher;
import com.funnydb.mutation.infra.MutationEventParser;
import com.funnydb.mutation.service.MutationEngine;
import com.funnydb.mutation.service.MutationMetrics;
import com.funnydb.mutation.service.PrometheusMetricsHttpServer;
import com.funnydb.mutation.service.MutationWorkerLauncher;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: App <config.properties>");
        }

        MutationAppConfig config;
        try (FileInputStream inputStream = new FileInputStream(args[0])) {
            config = MutationAppConfig.load(inputStream);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        MutationEngine engine = new MutationEngine();
        MutationMetrics metrics = new MutationMetrics();
        metrics.setRuntimeConfiguration(
                config.getWorkerThreadCount(),
                config.getTxBatchSize(),
                config.getTxFlushIntervalMs()
        );

        JedisPooled jedis = new JedisPooled(URI.create(config.getRedisAddress()));
        JedisRedisMutationStore store = new JedisRedisMutationStore(jedis, objectMapper, metrics);

        InetAddress addr = InetAddress.getLocalHost();
        String hostname = addr.getHostName();

        Properties kafkaProducerProperties = KafkaClientPropertiesFactory.buildProducerProperties(config, hostname);
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(kafkaProducerProperties);
        KafkaTransactionalSnapshotPublisher publisher = new KafkaTransactionalSnapshotPublisher(
                kafkaProducer,
                objectMapper,
                config.getKafkaConsumerGroupId(),
                metrics
        );

        AtomicBoolean running = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> running.set(false)));
        PrometheusMetricsHttpServer metricsHttpServer = new PrometheusMetricsHttpServer(
                config.getMetricsBindAddress(),
                config.getMetricsPort(),
                metrics
        );

        try {
            metricsHttpServer.start();
            publisher.initializeTransactions();
            LOG.info("Starting funnydb-mutation with workerThreadCount={}, batchSize={}, flushIntervalMs={}, metricsEndpoint=http://{}:{}/metrics",
                    config.getWorkerThreadCount(),
                    config.getTxBatchSize(),
                    config.getTxFlushIntervalMs(),
                    config.getMetricsBindAddress(),
                    config.getMetricsPort());

            MutationWorkerLauncher.WorkerLaunchSummary result = new MutationWorkerLauncher(
                    config.getWorkerThreadCount(),
                    engine,
                    store,
                    publisher,
                    config.getTxBatchSize(),
                    config.getTxFlushIntervalMs(),
                    Clock.systemUTC(),
                    metrics,
                    (workerIndex, coordinator, sharedMetrics) -> new com.funnydb.mutation.service.MutationWorker(
                            new KafkaMutationConsumer(
                                    new KafkaConsumer<>(KafkaClientPropertiesFactory.buildConsumerProperties(
                                            config,
                                            hostname + workerIndex
                                    )),
                                    new MutationEventParser(objectMapper),
                                    config.getKafkaConsumerInputTopic(),
                                    Duration.ofMillis(config.getTxFlushIntervalMs()),
                                    () -> coordinator.flush(com.funnydb.mutation.service.BatchFlushReason.PARTITION_REVOKED),
                                    sharedMetrics
                            ),
                            coordinator,
                            Clock.systemUTC(),
                            sharedMetrics
                    )
            ).runUntilStopped(() -> running.get() && !Thread.currentThread().isInterrupted());

            LOG.info("Mutation app finished with summary={} metrics={}", result.getProcessedCount(), metrics.snapshot());
            System.out.printf("Processed=%d invalid=%d flushed_batches=%d workers=%d metrics=%s%n",
                    result.getProcessedCount(),
                    result.getInvalidCount(),
                    result.getFlushedBatchCount(),
                    config.getWorkerThreadCount(),
                    metrics.snapshot());
        } finally {
            metricsHttpServer.close();
            publisher.close();
            store.close();
        }
    }
}
