package com.funnydb.mutation;

import com.funnydb.mutation.infra.JedisRedisMutationStore;
import com.funnydb.mutation.infra.KafkaTransactionalSnapshotPublisher;
import com.funnydb.mutation.model.ConsumedMutationMessage;
import com.funnydb.mutation.model.MutationData;
import com.funnydb.mutation.model.MutationEvent;
import com.funnydb.mutation.support.TestcontainersEnvironment;
import com.funnydb.mutation.service.BatchFlushReason;
import com.funnydb.mutation.service.MutationBatchAccumulator;
import com.funnydb.mutation.service.MutationCoordinator;
import com.funnydb.mutation.service.MutationEngine;
import com.funnydb.mutation.service.MutationMetrics;
import com.funnydb.mutation.service.MutationPipeline;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MutationCoordinatorTests {

    @Test
    public void shouldCommitOffsetsAndSnapshotsWhenBatchSizeReached() {
        MutationEngine engine = new MutationEngine();
        String inputTopic = TestcontainersEnvironment.uniqueName("mutation-input");
        String app = TestcontainersEnvironment.uniqueName("demo");
        String outputTopic = app + "-flink-users";
        String consumerGroupId = TestcontainersEnvironment.uniqueName("mutation-group");
        TestcontainersEnvironment.createTopic(inputTopic, 2);
        TestcontainersEnvironment.createTopic(outputTopic, 2);

        JedisRedisMutationStore store = TestcontainersEnvironment.newRedisStore();
        KafkaTransactionalSnapshotPublisher publisher = TestcontainersEnvironment.newPublisher(
                consumerGroupId,
                TestcontainersEnvironment.uniqueName("mutation-tx")
        );
        MutationCoordinator coordinator = new MutationCoordinator(
                new MutationPipeline(engine, store, new MutationMetrics()),
                publisher,
                2,
                10_000,
                Clock.fixed(Instant.ofEpochMilli(1000L), ZoneOffset.UTC),
                new MutationMetrics()
        );

        List<MutationBatchAccumulator.MutationBatch> flushed = coordinator.processMessages(List.of(
                message(inputTopic, app, 0, 0L, 1000L, "u-1", mapOf("score", 1)),
                message(inputTopic, app, 1, 1L, 1001L, "u-2", mapOf("score", 2))
        ), 2000L);

        assertEquals(1, flushed.size());
        assertEquals(BatchFlushReason.BATCH_SIZE_REACHED, flushed.get(0).getReason());
        assertEquals(2, TestcontainersEnvironment.consumeCommittedRecords(outputTopic, 2, java.time.Duration.ofSeconds(10)).size());
        assertEquals(Map.of(0, 1L, 1, 2L), TestcontainersEnvironment.readCommittedOffsets(consumerGroupId, inputTopic));

        publisher.close();
        store.close();
    }

    @Test
    public void shouldFlushRemainingMessagesOnShutdown() {
        MutationEngine engine = new MutationEngine();
        String inputTopic = TestcontainersEnvironment.uniqueName("mutation-input");
        String app = TestcontainersEnvironment.uniqueName("demo");
        String outputTopic = app + "-flink-users";
        String consumerGroupId = TestcontainersEnvironment.uniqueName("mutation-group");
        TestcontainersEnvironment.createTopic(inputTopic, 1);
        TestcontainersEnvironment.createTopic(outputTopic, 1);

        JedisRedisMutationStore store = TestcontainersEnvironment.newRedisStore();
        KafkaTransactionalSnapshotPublisher publisher = TestcontainersEnvironment.newPublisher(
                consumerGroupId,
                TestcontainersEnvironment.uniqueName("mutation-tx")
        );
        MutationCoordinator coordinator = new MutationCoordinator(
                new MutationPipeline(engine, store, new MutationMetrics()),
                publisher,
                10,
                10_000,
                Clock.fixed(Instant.ofEpochMilli(1000L), ZoneOffset.UTC),
                new MutationMetrics()
        );

        coordinator.processMessages(List.of(message(inputTopic, app, 0, 0L, 1000L, "u-1", mapOf("score", 1))), 2000L);
        MutationBatchAccumulator.MutationBatch batch = coordinator.flush(BatchFlushReason.SHUTDOWN);

        assertEquals(BatchFlushReason.SHUTDOWN, batch.getReason());
        assertEquals(1, batch.getSnapshots().size());
        assertEquals(1, batch.getOffsets().size());
        assertEquals(1, TestcontainersEnvironment.consumeCommittedRecords(outputTopic, 1, java.time.Duration.ofSeconds(10)).size());

        publisher.close();
        store.close();
    }

    private ConsumedMutationMessage message(String topic,
                                            String app,
                                            int partition,
                                            long offset,
                                            long eventTime,
                                            String identify,
                                            Map<String, Object> props) {
        return new ConsumedMutationMessage(
                topic,
                partition,
                offset,
                identify,
                event(app, eventTime, identify, props)
        );
    }

    private MutationEvent event(String app, long time, String identify, Map<String, Object> properties) {
        MutationData data = new MutationData();
        data.setIdentify(identify);
        data.setDataLifecycle("0");
        data.setOperate("set");
        data.setTime(time);
        data.setProperties(properties);

        MutationEvent event = new MutationEvent();
        event.setApp(app);
        event.setType("UserMutation");
        event.setData(data);
        return event;
    }

    private Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            values.put((String) kv[i], kv[i + 1]);
        }
        return values;
    }
}
