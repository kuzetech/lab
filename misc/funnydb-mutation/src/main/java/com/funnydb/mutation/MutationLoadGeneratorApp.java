package com.funnydb.mutation;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class MutationLoadGeneratorApp {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: MutationLoadGeneratorApp <config.properties>");
        }

        Properties properties = loadProperties(args[0]);
        LoadGeneratorConfig config = LoadGeneratorConfig.fromProperties(properties);
        Properties producerProperties = buildProducerProperties(config);

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties)) {
            runLoad(producer, config);
        }
    }

    private static void runLoad(KafkaProducer<String, String> producer, LoadGeneratorConfig config) {
        long startedAt = System.nanoTime();
        long nextTargetNanos = startedAt;
        long baseEventTime = System.currentTimeMillis();
        long reportIntervalNanos = Duration.ofMillis(config.getReportIntervalMs()).toNanos();
        long nextReportNanos = startedAt + reportIntervalNanos;

        for (long sequence = 0; sequence < config.getMessages(); sequence++) {
            if (config.getRatePerSecond() > 0) {
                nextTargetNanos += 1_000_000_000L / config.getRatePerSecond();
                sleepUntil(nextTargetNanos);
            }

            String identify = String.format("%s-%06d", config.getIdentifyPrefix(), sequence % config.getKeyCardinality());
            String key = config.getApp() + ":" + identify;
            long eventTime = baseEventTime + sequence;
            String payload = buildPayload(config, identify, eventTime, sequence);

            producer.send(new ProducerRecord<>(config.getTopic(), key, payload));

            long now = System.nanoTime();
            if (now >= nextReportNanos) {
                double elapsedSeconds = Math.max(0.001d, (now - startedAt) / 1_000_000_000.0d);
                long sentCount = sequence + 1;
                System.out.printf("loadgen sent=%d elapsed=%.2fs throughput=%.2f msg/s%n",
                        sentCount,
                        elapsedSeconds,
                        sentCount / elapsedSeconds);
                nextReportNanos += reportIntervalNanos;
            }
        }

        producer.flush();
        double elapsedSeconds = Math.max(0.001d, (System.nanoTime() - startedAt) / 1_000_000_000.0d);
        System.out.printf("loadgen completed sent=%d elapsed=%.2fs throughput=%.2f msg/s%n",
                config.getMessages(),
                elapsedSeconds,
                config.getMessages() / elapsedSeconds);
    }

    private static String buildPayload(LoadGeneratorConfig config, String identify, long eventTime, long sequence) {
        long ingestTime = eventTime + 3;
        String requestId = config.getApp() + "-req-" + sequence;
        String logId = config.getApp() + "-log-" + sequence;
        return "{"
                + "\"type\":\"" + config.getMutationType() + "\","
                + "\"data\":{"
                + "\"#operate\":\"set\","
                + "\"#time\":" + eventTime + ","
                + "\"#request_id\":\"" + requestId + "\","
                + "\"#sdk_version\":\"perf-1.0\","
                + "\"#log_id\":\"" + logId + "\","
                + "\"#sdk_type\":\"loadgen\","
                + "\"#identify\":\"" + identify + "\","
                + "\"properties\":{"
                + "\"score\":" + (sequence % 10000) + ","
                + "\"level\":" + (sequence % 100) + ","
                + "\"region\":\"ap-sg\","
                + "\"active\":" + ((sequence & 1) == 0)
                + "},"
                + "\"#ingest_host\":\"mutation-loadgen\","
                + "\"#data_lifecycle\":\"" + config.getDataLifecycle() + "\","
                + "\"#ingest_time\":" + ingestTime
                + "},"
                + "\"ip\":\"127.0.0.1\","
                + "\"app\":\"" + config.getApp() + "\","
                + "\"key\":\"" + config.getApp() + identify + "\","
                + "\"logId\":\"" + logId + "\","
                + "\"ingest_time\":" + ingestTime + ","
                + "\"access_id\":\"loadgen\""
                + "}";
    }

    private static void sleepUntil(long targetNanos) {
        while (true) {
            long remaining = targetNanos - System.nanoTime();
            if (remaining <= 0) {
                return;
            }
            if (remaining > 2_000_000L) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    return;
                }
            } else {
                Thread.onSpinWait();
            }
        }
    }

    private static Properties loadProperties(String path) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(path)) {
            properties.load(inputStream);
        }
        return properties;
    }

    private static Properties buildProducerProperties(LoadGeneratorConfig config) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getKafkaBootstrapServers());
        properties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, config.getClientId());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "1");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, String.valueOf(128 * 1024));
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }

    private static final class LoadGeneratorConfig {
        private final String kafkaBootstrapServers;
        private final String topic;
        private final long messages;
        private final long ratePerSecond;
        private final int keyCardinality;
        private final String app;
        private final String mutationType;
        private final String dataLifecycle;
        private final String identifyPrefix;
        private final long reportIntervalMs;
        private final String clientId;

        private LoadGeneratorConfig(String kafkaBootstrapServers,
                                    String topic,
                                    long messages,
                                    long ratePerSecond,
                                    int keyCardinality,
                                    String app,
                                    String mutationType,
                                    String dataLifecycle,
                                    String identifyPrefix,
                                    long reportIntervalMs,
                                    String clientId) {
            this.kafkaBootstrapServers = kafkaBootstrapServers;
            this.topic = topic;
            this.messages = messages;
            this.ratePerSecond = ratePerSecond;
            this.keyCardinality = keyCardinality;
            this.app = app;
            this.mutationType = mutationType;
            this.dataLifecycle = dataLifecycle;
            this.identifyPrefix = identifyPrefix;
            this.reportIntervalMs = reportIntervalMs;
            this.clientId = clientId;
        }

        private static LoadGeneratorConfig fromProperties(Properties properties) {
            return new LoadGeneratorConfig(
                    require(properties, "kafka.bootstrap.servers"),
                    properties.getProperty("load.topic", "funnydb-ingest-mutation-events"),
                    Long.parseLong(properties.getProperty("load.messages", "100000")),
                    Long.parseLong(properties.getProperty("load.rate-per-second", "5000")),
                    Integer.parseInt(properties.getProperty("load.key-cardinality", "20000")),
                    properties.getProperty("load.app", "perf-demo"),
                    properties.getProperty("load.mutation-type", "UserMutation"),
                    properties.getProperty("load.data-lifecycle", "0"),
                    properties.getProperty("load.identify-prefix", "user"),
                    Long.parseLong(properties.getProperty("load.report-interval-ms", "5000")),
                    properties.getProperty("load.producer.client-id", "mutation-loadgen")
            );
        }

        private String getKafkaBootstrapServers() {
            return kafkaBootstrapServers;
        }

        private String getTopic() {
            return topic;
        }

        private long getMessages() {
            return messages;
        }

        private long getRatePerSecond() {
            return ratePerSecond;
        }

        private int getKeyCardinality() {
            return keyCardinality;
        }

        private String getApp() {
            return app;
        }

        private String getMutationType() {
            return mutationType;
        }

        private String getDataLifecycle() {
            return dataLifecycle;
        }

        private String getIdentifyPrefix() {
            return identifyPrefix;
        }

        private long getReportIntervalMs() {
            return reportIntervalMs;
        }

        private String getClientId() {
            return clientId;
        }

        private static String require(Properties properties, String key) {
            String value = properties.getProperty(key);
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Missing config: " + key);
            }
            return value;
        }
    }
}
