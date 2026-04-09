package com.funnydb.mutation.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class MutationMetrics {
    private final LongAdder processedMessages = new LongAdder();
    private final LongAdder invalidMessages = new LongAdder();
    private final LongAdder ignoredOldEvents = new LongAdder();
    private final LongAdder invalidFieldTypeEvents = new LongAdder();
    private final LongAdder flushedBatches = new LongAdder();
    private final LongAdder publishedSnapshots = new LongAdder();
    private final LongAdder publishedOffsets = new LongAdder();
    private final LongAdder rebalanceCount = new LongAdder();
    private final LongAdder polledRecords = new LongAdder();
    private final AtomicInteger configuredWorkerThreads = new AtomicInteger();
    private final AtomicInteger configuredBatchSize = new AtomicInteger();
    private final AtomicLong configuredFlushIntervalMs = new AtomicLong();

    private final Histogram consumerPollDurationSeconds = new Histogram(0.001, 0.005, 0.01, 0.05, 0.1, 0.25, 0.5, 1, 2, 5);
    private final Histogram redisApplyDurationSeconds = new Histogram(0.0005, 0.001, 0.005, 0.01, 0.05, 0.1, 0.25, 0.5, 1);
    private final Histogram mutationProcessingDurationSeconds = new Histogram(0.0005, 0.001, 0.005, 0.01, 0.05, 0.1, 0.25, 0.5, 1);
    private final Histogram kafkaPublishBatchDurationSeconds = new Histogram(0.001, 0.005, 0.01, 0.05, 0.1, 0.25, 0.5, 1, 2, 5, 10);
    private final Histogram batchSnapshotSize = new Histogram(1, 5, 10, 25, 50, 100, 200, 500, 1000);

    public void recordProcessedMessages(long count) {
        processedMessages.add(count);
    }

    public void recordInvalidMessages(long count) {
        invalidMessages.add(count);
    }

    public void recordMutationStatus(MutationStatus status) {
        if (status == MutationStatus.IGNORED_OLD_EVENT) {
            ignoredOldEvents.increment();
        } else if (status == MutationStatus.INVALID_FIELD_TYPE) {
            invalidFieldTypeEvents.increment();
        }
    }

    public void recordFlushedBatch(long snapshotCount) {
        recordFlushedBatch(snapshotCount, 0L);
    }

    public void recordFlushedBatch(long snapshotCount, long offsetCount) {
        flushedBatches.increment();
        publishedSnapshots.add(snapshotCount);
        publishedOffsets.add(offsetCount);
        batchSnapshotSize.record(snapshotCount);
    }

    public void recordRebalance() {
        rebalanceCount.increment();
    }

    public void recordPoll(long recordCount, long durationNanos) {
        polledRecords.add(recordCount);
        consumerPollDurationSeconds.record(nanosToSeconds(durationNanos));
    }

    public void recordRedisApply(long durationNanos) {
        redisApplyDurationSeconds.record(nanosToSeconds(durationNanos));
    }

    public void recordMessageProcessing(long durationNanos) {
        mutationProcessingDurationSeconds.record(nanosToSeconds(durationNanos));
    }

    public void recordPublishBatch(long durationNanos, long snapshotCount, long offsetCount) {
        kafkaPublishBatchDurationSeconds.record(nanosToSeconds(durationNanos));
        recordFlushedBatch(snapshotCount, offsetCount);
    }

    public void setRuntimeConfiguration(int workerThreadCount, int batchSize, long flushIntervalMs) {
        configuredWorkerThreads.set(workerThreadCount);
        configuredBatchSize.set(batchSize);
        configuredFlushIntervalMs.set(flushIntervalMs);
    }

    public Map<String, Long> snapshot() {
        Map<String, Long> values = new LinkedHashMap<>();
        values.put("processed_messages", processedMessages.sum());
        values.put("invalid_messages", invalidMessages.sum());
        values.put("ignored_old_events", ignoredOldEvents.sum());
        values.put("invalid_field_type_events", invalidFieldTypeEvents.sum());
        values.put("flushed_batches", flushedBatches.sum());
        values.put("published_snapshots", publishedSnapshots.sum());
        values.put("published_offsets", publishedOffsets.sum());
        values.put("polled_records", polledRecords.sum());
        values.put("rebalance_count", rebalanceCount.sum());
        return values;
    }

    public String renderPrometheus() {
        StringBuilder builder = new StringBuilder(4096);
        appendCounter(builder, "mutation_processed_messages_total", "Total valid mutation messages processed.", processedMessages.sum());
        appendCounter(builder, "mutation_invalid_messages_total", "Total invalid mutation messages.", invalidMessages.sum());
        appendCounter(builder, "mutation_ignored_old_events_total", "Total old events ignored by Redis timestamp semantics.", ignoredOldEvents.sum());
        appendCounter(builder, "mutation_invalid_field_type_events_total", "Total events ignored because of invalid field types.", invalidFieldTypeEvents.sum());
        appendCounter(builder, "mutation_flushed_batches_total", "Total published transactional batches.", flushedBatches.sum());
        appendCounter(builder, "mutation_published_snapshots_total", "Total snapshots published to Kafka.", publishedSnapshots.sum());
        appendCounter(builder, "mutation_published_offsets_total", "Total consumer offsets committed transactionally.", publishedOffsets.sum());
        appendCounter(builder, "mutation_consumer_polled_records_total", "Total Kafka records returned by poll.", polledRecords.sum());
        appendCounter(builder, "mutation_consumer_rebalances_total", "Total Kafka consumer rebalances observed.", rebalanceCount.sum());

        appendGauge(builder, "mutation_configured_worker_threads", "Configured worker thread count.", configuredWorkerThreads.get());
        appendGauge(builder, "mutation_configured_batch_size", "Configured transaction batch size.", configuredBatchSize.get());
        appendGauge(builder, "mutation_configured_flush_interval_ms", "Configured transaction flush interval in milliseconds.", configuredFlushIntervalMs.get());

        appendGauge(builder, "mutation_jvm_threads_live", "Current live JVM thread count.", Thread.activeCount());
        Runtime runtime = Runtime.getRuntime();
        appendGauge(builder, "mutation_jvm_memory_used_bytes", "Current used JVM heap bytes.", runtime.totalMemory() - runtime.freeMemory());
        appendGauge(builder, "mutation_jvm_memory_max_bytes", "Max JVM heap bytes.", runtime.maxMemory());

        consumerPollDurationSeconds.append(builder, "mutation_consumer_poll_seconds", "Kafka consumer poll duration in seconds.");
        redisApplyDurationSeconds.append(builder, "mutation_redis_apply_seconds", "Redis Lua apply duration in seconds.");
        mutationProcessingDurationSeconds.append(builder, "mutation_message_processing_seconds", "End-to-end per-message processing duration in seconds.");
        kafkaPublishBatchDurationSeconds.append(builder, "mutation_kafka_publish_batch_seconds", "Kafka transactional publish batch duration in seconds.");
        batchSnapshotSize.append(builder, "mutation_batch_snapshot_size", "Published snapshot count per flushed batch.");
        return builder.toString();
    }

    private static void appendCounter(StringBuilder builder, String name, String help, long value) {
        builder.append("# HELP ").append(name).append(' ').append(help).append('\n');
        builder.append("# TYPE ").append(name).append(" counter\n");
        builder.append(name).append(' ').append(value).append('\n');
    }

    private static void appendGauge(StringBuilder builder, String name, String help, long value) {
        builder.append("# HELP ").append(name).append(' ').append(help).append('\n');
        builder.append("# TYPE ").append(name).append(" gauge\n");
        builder.append(name).append(' ').append(value).append('\n');
    }

    private static double nanosToSeconds(long durationNanos) {
        return durationNanos / 1_000_000_000.0d;
    }

    private static final class Histogram {
        private final double[] buckets;
        private final LongAdder[] counts;
        private final DoubleAdderWithLongAdder sum = new DoubleAdderWithLongAdder();
        private final LongAdder totalCount = new LongAdder();

        private Histogram(double... buckets) {
            this.buckets = buckets;
            this.counts = new LongAdder[buckets.length];
            for (int i = 0; i < buckets.length; i++) {
                this.counts[i] = new LongAdder();
            }
        }

        private void record(double value) {
            totalCount.increment();
            sum.add(value);
            for (int i = 0; i < buckets.length; i++) {
                if (value <= buckets[i]) {
                    counts[i].increment();
                }
            }
        }

        private void append(StringBuilder builder, String name, String help) {
            builder.append("# HELP ").append(name).append(' ').append(help).append('\n');
            builder.append("# TYPE ").append(name).append(" histogram\n");
            for (int i = 0; i < buckets.length; i++) {
                builder.append(name)
                        .append("_bucket{le=\"")
                        .append(formatDouble(buckets[i]))
                        .append("\"} ")
                        .append(counts[i].sum())
                        .append('\n');
            }
            builder.append(name).append("_bucket{le=\"+Inf\"} ").append(totalCount.sum()).append('\n');
            builder.append(name).append("_sum ").append(formatDouble(sum.sum())).append('\n');
            builder.append(name).append("_count ").append(totalCount.sum()).append('\n');
        }

        private static String formatDouble(double value) {
            return String.format(Locale.US, "%.6f", value);
        }
    }

    private static final class DoubleAdderWithLongAdder {
        private final AtomicLong bits = new AtomicLong(Double.doubleToRawLongBits(0.0d));

        private void add(double delta) {
            long currentBits;
            long nextBits;
            do {
                currentBits = bits.get();
                double current = Double.longBitsToDouble(currentBits);
                nextBits = Double.doubleToRawLongBits(current + delta);
            } while (!bits.compareAndSet(currentBits, nextBits));
        }

        private double sum() {
            return Double.longBitsToDouble(bits.get());
        }
    }
}
