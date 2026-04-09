package com.funnydb.mutation.service;

import com.funnydb.mutation.model.ProducedSnapshotMessage;
import com.funnydb.mutation.model.TopicPartitionOffset;

import java.time.Clock;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MutationBatchAccumulator {
    private final int batchSize;
    private final long flushIntervalMs;
    private final Clock clock;
    private final List<ProducedSnapshotMessage> snapshots = new ArrayList<>();
    private final Map<String, TopicPartitionOffset> offsets = new LinkedHashMap<>();
    private long firstBufferedAt = -1L;

    public MutationBatchAccumulator(int batchSize, long flushIntervalMs, Clock clock) {
        this.batchSize = batchSize;
        this.flushIntervalMs = flushIntervalMs;
        this.clock = clock;
    }

    public void append(MutationProcessingResult result) {
        if (snapshots.isEmpty()) {
            firstBufferedAt = clock.millis();
        }
        snapshots.add(result.getSnapshotMessage());
        TopicPartitionOffset offset = result.getOffsetToCommit();
        String key = offset.getTopic() + "-" + offset.getPartition();
        offsets.put(key, offset);
    }

    public boolean shouldFlush() {
        return snapshots.size() >= batchSize || isFlushIntervalReached();
    }

    public boolean isFlushIntervalReached() {
        return !snapshots.isEmpty() && clock.millis() - firstBufferedAt >= flushIntervalMs;
    }

    public boolean isEmpty() {
        return snapshots.isEmpty();
    }

    public MutationBatch drain(BatchFlushReason reason) {
        MutationBatch batch = new MutationBatch(reason, new ArrayList<>(snapshots), new ArrayList<>(offsets.values()));
        snapshots.clear();
        offsets.clear();
        firstBufferedAt = -1L;
        return batch;
    }

    public static class MutationBatch {
        private final BatchFlushReason reason;
        private final List<ProducedSnapshotMessage> snapshots;
        private final List<TopicPartitionOffset> offsets;

        public MutationBatch(BatchFlushReason reason,
                             List<ProducedSnapshotMessage> snapshots,
                             List<TopicPartitionOffset> offsets) {
            this.reason = reason;
            this.snapshots = snapshots;
            this.offsets = offsets;
        }

        public BatchFlushReason getReason() {
            return reason;
        }

        public List<ProducedSnapshotMessage> getSnapshots() {
            return snapshots;
        }

        public List<TopicPartitionOffset> getOffsets() {
            return offsets;
        }
    }
}
