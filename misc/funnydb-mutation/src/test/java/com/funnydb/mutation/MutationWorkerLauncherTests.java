package com.funnydb.mutation;

import com.funnydb.mutation.infra.JedisRedisMutationStore;
import com.funnydb.mutation.infra.KafkaTransactionalSnapshotPublisher;
import com.funnydb.mutation.support.TestcontainersEnvironment;
import com.funnydb.mutation.service.MutationEngine;
import com.funnydb.mutation.service.MutationMetrics;
import com.funnydb.mutation.service.MutationWorker;
import com.funnydb.mutation.service.MutationWorkerLauncher;
import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertEquals;

public class MutationWorkerLauncherTests {

    @Test
    public void shouldRunMultipleWorkersAndAggregateSummary() {
        MutationEngine engine = new MutationEngine();
        String inputTopic = TestcontainersEnvironment.uniqueName("mutation-input");
        String app = TestcontainersEnvironment.uniqueName("demo");
        String outputTopic = app + "-flink-users";
        String consumerGroupId = TestcontainersEnvironment.uniqueName("mutation-group");
        TestcontainersEnvironment.createTopic(inputTopic, 2);
        TestcontainersEnvironment.createTopic(outputTopic, 2);
        TestcontainersEnvironment.produceJsonMessages(inputTopic, List.of(
                TestcontainersEnvironment.jsonMessage(0, "u-0", TestData.event(app, 1000L, "u-0", TestData.mapOf("score", 1))),
                TestcontainersEnvironment.jsonMessage(1, "u-1", TestData.event(app, 1001L, "u-1", TestData.mapOf("score", 1)))
        ));

        JedisRedisMutationStore store = TestcontainersEnvironment.newRedisStore();
        KafkaTransactionalSnapshotPublisher publisher = TestcontainersEnvironment.newPublisher(
                consumerGroupId,
                TestcontainersEnvironment.uniqueName("mutation-tx")
        );
        MutationWorkerLauncher.WorkerLaunchSummary summary = new MutationWorkerLauncher(
                2,
                engine,
                store,
                publisher,
                10,
                10_000L,
                Clock.fixed(Instant.ofEpochMilli(2000L), ZoneOffset.UTC),
                new MutationMetrics(),
                (workerIndex, coordinator, metrics) -> new MutationWorker(
                        TestcontainersEnvironment.newMutationConsumer(
                                inputTopic,
                                consumerGroupId,
                                Duration.ofMillis(200),
                                metrics
                        ),
                        coordinator,
                        Clock.fixed(Instant.ofEpochMilli(2000L), ZoneOffset.UTC),
                        metrics
                )
        ).runUntilStopped(new StopAfterDelay(Duration.ofSeconds(3)));

        assertEquals(2, summary.getProcessedCount());
        assertEquals(0, summary.getInvalidCount());
        assertEquals(2, TestcontainersEnvironment.consumeCommittedRecords(outputTopic, 2, Duration.ofSeconds(10)).size());

        publisher.close();
        store.close();
    }

    private static class StopAfterDelay implements BooleanSupplier {
        private final long deadlineNanos;

        private StopAfterDelay(Duration duration) {
            this.deadlineNanos = System.nanoTime() + duration.toNanos();
        }

        @Override
        public boolean getAsBoolean() {
            return System.nanoTime() < deadlineNanos;
        }
    }
}
