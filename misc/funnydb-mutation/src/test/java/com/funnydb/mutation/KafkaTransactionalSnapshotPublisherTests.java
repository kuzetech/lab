package com.funnydb.mutation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funnydb.mutation.infra.KafkaTransactionalSnapshotPublisher;
import com.funnydb.mutation.model.ProducedSnapshotMessage;
import com.funnydb.mutation.model.TopicPartitionOffset;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KafkaTransactionalSnapshotPublisherTests {

    @Test
    public void shouldPublishSnapshotsWithinTransaction() {
        MockProducer<String, String> producer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());
        KafkaTransactionalSnapshotPublisher publisher = new KafkaTransactionalSnapshotPublisher(
                producer,
                new ObjectMapper(),
                "mutation-group"
        );
        publisher.initializeTransactions();

        publisher.publishBatch(
                Collections.singletonList(new ProducedSnapshotMessage("demo-flink-users", "u-1", snapshot())),
                Collections.singletonList(new TopicPartitionOffset("funnydb-ingest-mutation-events", 0, 1L))
        );

        assertEquals(1, producer.history().size());
        assertEquals("demo-flink-users", producer.history().get(0).topic());
        assertTrue(producer.transactionCommitted());
    }

    private Map<String, Object> snapshot() {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("#user_id", "u-1");
        snapshot.put("#event_time", 1000L);
        return snapshot;
    }
}
