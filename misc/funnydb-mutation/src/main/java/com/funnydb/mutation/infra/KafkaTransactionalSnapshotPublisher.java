package com.funnydb.mutation.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funnydb.mutation.model.ProducedSnapshotMessage;
import com.funnydb.mutation.model.TopicPartitionOffset;
import com.funnydb.mutation.service.MutationMetrics;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KafkaTransactionalSnapshotPublisher implements TransactionalSnapshotPublisher {
    private final Producer<String, String> producer;
    private final ObjectMapper objectMapper;
    private final String consumerGroupId;
    private final MutationMetrics metrics;

    public KafkaTransactionalSnapshotPublisher(Producer<String, String> producer,
                                               ObjectMapper objectMapper,
                                               String consumerGroupId) {
        this(producer, objectMapper, consumerGroupId, new MutationMetrics());
    }

    public KafkaTransactionalSnapshotPublisher(Producer<String, String> producer,
                                               ObjectMapper objectMapper,
                                               String consumerGroupId,
                                               MutationMetrics metrics) {
        this.producer = producer;
        this.objectMapper = objectMapper;
        this.consumerGroupId = consumerGroupId;
        this.metrics = metrics;
    }

    public void initializeTransactions() {
        producer.initTransactions();
    }

    @Override
    public synchronized void publishBatch(List<ProducedSnapshotMessage> snapshots, List<TopicPartitionOffset> offsets) {
        long startNanos = System.nanoTime();
        producer.beginTransaction();
        try {
            for (ProducedSnapshotMessage snapshot : snapshots) {
                producer.send(new ProducerRecord<>(
                        snapshot.getTopic(),
                        snapshot.getKey(),
                        objectMapper.writeValueAsString(snapshot.getSnapshot())
                ));
            }
            producer.sendOffsetsToTransaction(toKafkaOffsets(offsets), consumerGroupId);
            producer.commitTransaction();
            metrics.recordPublishBatch(System.nanoTime() - startNanos, snapshots.size(), offsets.size());
        } catch (JsonProcessingException exception) {
            producer.abortTransaction();
            throw new IllegalStateException("Failed to serialize snapshot", exception);
        } catch (RuntimeException exception) {
            producer.abortTransaction();
            throw exception;
        }
    }

    public void close() {
        producer.close();
    }

    private Map<TopicPartition, OffsetAndMetadata> toKafkaOffsets(List<TopicPartitionOffset> offsets) {
        Map<TopicPartition, OffsetAndMetadata> mapped = new LinkedHashMap<>();
        for (TopicPartitionOffset offset : offsets) {
            mapped.put(
                    new TopicPartition(offset.getTopic(), offset.getPartition()),
                    new OffsetAndMetadata(offset.getNextOffset())
            );
        }
        return mapped;
    }
}
