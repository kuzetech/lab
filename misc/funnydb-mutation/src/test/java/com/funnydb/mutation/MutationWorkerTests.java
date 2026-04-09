package com.funnydb.mutation;

import com.funnydb.mutation.infra.JedisRedisMutationStore;
import com.funnydb.mutation.infra.KafkaMutationConsumer;
import com.funnydb.mutation.infra.KafkaTransactionalSnapshotPublisher;
import com.funnydb.mutation.support.TestcontainersEnvironment;
import com.funnydb.mutation.service.MutationCoordinator;
import com.funnydb.mutation.service.MutationEngine;
import com.funnydb.mutation.service.MutationMetrics;
import com.funnydb.mutation.service.MutationPipeline;
import com.funnydb.mutation.service.MutationWorker;
import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MutationWorkerTests {

    @Test
    public void shouldRunWorkerAndFlushAtShutdown() {
        MutationEngine engine = new MutationEngine();
        String inputTopic = TestcontainersEnvironment.uniqueName("mutation-input");
        String app = TestcontainersEnvironment.uniqueName("demo");
        String outputTopic = app + "-flink-users";
        String consumerGroupId = TestcontainersEnvironment.uniqueName("mutation-group");
        TestcontainersEnvironment.createTopic(inputTopic, 1);
        TestcontainersEnvironment.createTopic(outputTopic, 1);
        TestcontainersEnvironment.produceJsonMessages(inputTopic, List.of(
                TestcontainersEnvironment.jsonMessage(0, "u-1", TestData.event(app, 1000L, "u-1", TestData.mapOf("score", 1))),
                TestcontainersEnvironment.jsonMessage(0, "u-2", TestData.event(app, 1001L, "u-2", TestData.mapOf("score", 1)))
        ));

        JedisRedisMutationStore store = TestcontainersEnvironment.newRedisStore();
        KafkaTransactionalSnapshotPublisher publisher = TestcontainersEnvironment.newPublisher(
                consumerGroupId,
                TestcontainersEnvironment.uniqueName("mutation-tx")
        );
        MutationCoordinator coordinator = new MutationCoordinator(
                new MutationPipeline(engine, store, new MutationMetrics()),
                publisher,
                10,
                10_000L,
                Clock.fixed(Instant.ofEpochMilli(2000L), ZoneOffset.UTC),
                new MutationMetrics()
        );
        KafkaMutationConsumer consumer = TestcontainersEnvironment.newMutationConsumer(
                inputTopic,
                consumerGroupId,
                Duration.ofMillis(200),
                new MutationMetrics()
        );
        MutationWorker worker = new MutationWorker(
                consumer,
                coordinator,
                Clock.fixed(Instant.ofEpochMilli(3000L), ZoneOffset.UTC),
                new MutationMetrics()
        );

        MutationWorker.WorkerRunResult result = worker.runUntilDrained();

        assertEquals(2, result.getProcessedCount());
        assertEquals(0, result.getInvalidCount());
        assertEquals(1, result.getFlushedBatches().size());
        assertEquals(2, TestcontainersEnvironment.consumeCommittedRecords(outputTopic, 2, Duration.ofSeconds(10)).size());

        publisher.close();
        store.close();
    }

    @Test
    public void shouldCountInvalidMessagesFromPollBatch() {
        MutationEngine engine = new MutationEngine();
        String inputTopic = TestcontainersEnvironment.uniqueName("mutation-input");
        String app = TestcontainersEnvironment.uniqueName("demo");
        String outputTopic = app + "-flink-users";
        String consumerGroupId = TestcontainersEnvironment.uniqueName("mutation-group");
        TestcontainersEnvironment.createTopic(inputTopic, 1);
        TestcontainersEnvironment.createTopic(outputTopic, 1);
        TestcontainersEnvironment.produceJsonMessages(inputTopic, List.of(
                TestcontainersEnvironment.jsonMessage(0, "u-1", TestData.event(app, 1000L, "u-1", TestData.mapOf("score", 1))),
                TestcontainersEnvironment.rawMessage(0, "broken-1", "{\"type\":\"UserMutation\"}"),
                TestcontainersEnvironment.rawMessage(0, "broken-2", "{\"app\":\"demo\"}")
        ));

        JedisRedisMutationStore store = TestcontainersEnvironment.newRedisStore();
        KafkaTransactionalSnapshotPublisher publisher = TestcontainersEnvironment.newPublisher(
                consumerGroupId,
                TestcontainersEnvironment.uniqueName("mutation-tx")
        );
        MutationCoordinator coordinator = new MutationCoordinator(
                new MutationPipeline(engine, store, new MutationMetrics()),
                publisher,
                10,
                10_000L,
                Clock.fixed(Instant.ofEpochMilli(2000L), ZoneOffset.UTC),
                new MutationMetrics()
        );
        KafkaMutationConsumer consumer = TestcontainersEnvironment.newMutationConsumer(
                inputTopic,
                consumerGroupId,
                Duration.ofMillis(200),
                new MutationMetrics()
        );
        MutationWorker worker = new MutationWorker(
                consumer,
                coordinator,
                Clock.fixed(Instant.ofEpochMilli(3000L), ZoneOffset.UTC),
                new MutationMetrics()
        );

        MutationWorker.WorkerRunResult result = worker.runUntilDrained();

        assertEquals(1, result.getProcessedCount());
        assertEquals(2, result.getInvalidCount());
        assertEquals(1, TestcontainersEnvironment.consumeCommittedRecords(outputTopic, 1, Duration.ofSeconds(10)).size());

        publisher.close();
        store.close();
    }
}
