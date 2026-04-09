package com.funnydb.mutation;

import com.funnydb.mutation.service.MutationMetrics;
import com.funnydb.mutation.service.MutationStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MutationMetricsTests {

    @Test
    public void shouldTrackCoreCounters() {
        MutationMetrics metrics = new MutationMetrics();
        metrics.recordProcessedMessages(3);
        metrics.recordInvalidMessages(2);
        metrics.recordMutationStatus(MutationStatus.IGNORED_OLD_EVENT);
        metrics.recordMutationStatus(MutationStatus.INVALID_FIELD_TYPE);
        metrics.recordPoll(7, 10_000_000L);
        metrics.recordRedisApply(1_000_000L);
        metrics.recordMessageProcessing(2_000_000L);
        metrics.recordPublishBatch(3_000_000L, 5, 5);
        metrics.recordRebalance();
        metrics.setRuntimeConfiguration(4, 200, 1000L);

        assertEquals(3L, metrics.snapshot().get("processed_messages").longValue());
        assertEquals(2L, metrics.snapshot().get("invalid_messages").longValue());
        assertEquals(1L, metrics.snapshot().get("ignored_old_events").longValue());
        assertEquals(1L, metrics.snapshot().get("invalid_field_type_events").longValue());
        assertEquals(1L, metrics.snapshot().get("flushed_batches").longValue());
        assertEquals(5L, metrics.snapshot().get("published_snapshots").longValue());
        assertEquals(5L, metrics.snapshot().get("published_offsets").longValue());
        assertEquals(7L, metrics.snapshot().get("polled_records").longValue());
        assertEquals(1L, metrics.snapshot().get("rebalance_count").longValue());

        String prometheus = metrics.renderPrometheus();
        assertTrue(prometheus.contains("mutation_processed_messages_total 3"));
        assertTrue(prometheus.contains("mutation_kafka_publish_batch_seconds_bucket"));
        assertTrue(prometheus.contains("mutation_configured_worker_threads 4"));
    }
}
