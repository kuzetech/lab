package com.funnydb.mutation.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funnydb.mutation.infra.JedisRedisMutationStore;
import com.funnydb.mutation.infra.KafkaMutationConsumer;
import com.funnydb.mutation.infra.KafkaTransactionalSnapshotPublisher;
import com.funnydb.mutation.infra.MutationEventParser;
import com.funnydb.mutation.service.MutationMetrics;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.JedisPooled;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public final class TestcontainersEnvironment {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse("apache/kafka-native:3.8.0")
    );
    private static final GenericContainer<?> REDIS = new GenericContainer<>(
            DockerImageName.parse("redis/redis-stack-server:7.2.0-v10")
    )
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    static {
        KAFKA.start();
        REDIS.start();
    }

    private TestcontainersEnvironment() {
    }

    public static String uniqueName(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().replace("-", "");
    }

    public static ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }

    public static void createTopic(String topic, int partitions) {
        try (AdminClient admin = AdminClient.create(Map.of(
                "bootstrap.servers", KAFKA.getBootstrapServers()
        ))) {
            admin.createTopics(List.of(new NewTopic(topic, partitions, (short) 1))).all().get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while creating topic " + topic, exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Failed to create topic " + topic, exception);
        }
    }

    public static JedisRedisMutationStore newRedisStore() {
        return new JedisRedisMutationStore(
                new JedisPooled(REDIS.getHost(), REDIS.getMappedPort(6379)),
                OBJECT_MAPPER
        );
    }

    public static void flushRedis() {
        try (JedisPooled jedis = new JedisPooled(REDIS.getHost(), REDIS.getMappedPort(6379))) {
            jedis.flushDB();
        }
    }

    public static KafkaTransactionalSnapshotPublisher newPublisher(String consumerGroupId, String transactionalId) {
        KafkaTransactionalSnapshotPublisher publisher = new KafkaTransactionalSnapshotPublisher(
                new KafkaProducer<>(publisherProperties(transactionalId)),
                OBJECT_MAPPER,
                consumerGroupId
        );
        publisher.initializeTransactions();
        return publisher;
    }

    public static KafkaMutationConsumer newMutationConsumer(String inputTopic,
                                                            String consumerGroupId,
                                                            Duration pollTimeout,
                                                            MutationMetrics metrics) {
        return new KafkaMutationConsumer(
                new KafkaConsumer<>(consumerProperties(consumerGroupId)),
                new MutationEventParser(OBJECT_MAPPER),
                inputTopic,
                pollTimeout,
                () -> {
                },
                metrics
        );
    }

    public static void produceJsonMessages(String topic, Collection<KafkaInputMessage> messages) {
        try (Producer<String, String> producer = new KafkaProducer<>(plainProducerProperties())) {
            for (KafkaInputMessage message : messages) {
                ProducerRecord<String, String> record = new ProducerRecord<>(
                        topic,
                        message.partition,
                        null,
                        message.key,
                        message.payload
                );
                producer.send(record).get();
            }
            producer.flush();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while producing test messages", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Failed to produce test messages", exception);
        }
    }

    public static List<ConsumerRecord<String, String>> consumeCommittedRecords(String topic,
                                                                               int expectedCount,
                                                                               Duration timeout) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, uniqueName("output-reader"));
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.toString().toLowerCase());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        List<ConsumerRecord<String, String>> records = new ArrayList<>();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(List.of(topic));
            long deadline = System.nanoTime() + timeout.toNanos();
            while (System.nanoTime() < deadline && records.size() < expectedCount) {
                ConsumerRecords<String, String> polled = consumer.poll(Duration.ofMillis(200));
                polled.forEach(records::add);
            }
        }
        return records;
    }

    public static Map<Integer, Long> readCommittedOffsets(String consumerGroupId, String topic) {
        try (AdminClient admin = AdminClient.create(Map.of(
                "bootstrap.servers", KAFKA.getBootstrapServers()
        ))) {
            return admin.listConsumerGroupOffsets(consumerGroupId)
                    .partitionsToOffsetAndMetadata()
                    .get()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().topic().equals(topic))
                    .collect(java.util.stream.Collectors.toMap(
                            entry -> entry.getKey().partition(),
                            entry -> entry.getValue().offset()
                    ));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while reading committed offsets", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Failed to read committed offsets", exception);
        }
    }

    public static KafkaInputMessage jsonMessage(int partition, String key, Object payload) {
        try {
            return new KafkaInputMessage(partition, key, OBJECT_MAPPER.writeValueAsString(payload));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize test payload", exception);
        }
    }

    public static KafkaInputMessage rawMessage(int partition, String key, String payload) {
        return new KafkaInputMessage(partition, key, payload);
    }

    public static String snapshotValue(ConsumerRecord<String, String> record, String field) {
        try {
            return OBJECT_MAPPER.readTree(record.value()).path(field).asText(null);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to parse snapshot payload", exception);
        }
    }

    public static long snapshotLongValue(ConsumerRecord<String, String> record, String field) {
        try {
            return OBJECT_MAPPER.readTree(record.value()).path(field).asLong();
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to parse snapshot payload", exception);
        }
    }

    private static Properties publisherProperties(String transactionalId) {
        Properties properties = plainProducerProperties();
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId);
        return properties;
    }

    private static Properties plainProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }

    private static Properties consumerProperties(String consumerGroupId) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return properties;
    }

    public static final class KafkaInputMessage {
        private final int partition;
        private final String key;
        private final String payload;

        private KafkaInputMessage(int partition, String key, String payload) {
            this.partition = partition;
            this.key = key;
            this.payload = payload;
        }
    }
}
