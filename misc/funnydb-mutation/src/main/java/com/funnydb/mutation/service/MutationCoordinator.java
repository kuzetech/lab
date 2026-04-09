package com.funnydb.mutation.service;

import com.funnydb.mutation.infra.TransactionalSnapshotPublisher;
import com.funnydb.mutation.model.ConsumedMutationMessage;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

public class MutationCoordinator {
    private final MutationPipeline pipeline;
    private final TransactionalSnapshotPublisher publisher;
    private final MutationBatchAccumulator accumulator;
    private final MutationMetrics metrics;

    public MutationCoordinator(MutationPipeline pipeline,
                               TransactionalSnapshotPublisher publisher,
                               int batchSize,
                               long flushIntervalMs,
                               Clock clock,
                               MutationMetrics metrics) {
        this.pipeline = pipeline;
        this.publisher = publisher;
        this.accumulator = new MutationBatchAccumulator(batchSize, flushIntervalMs, clock);
        this.metrics = metrics;
    }

    public List<MutationBatchAccumulator.MutationBatch> processMessages(List<ConsumedMutationMessage> messages, long updatedTime) {
        List<MutationBatchAccumulator.MutationBatch> flushedBatches = new ArrayList<>();
        for (ConsumedMutationMessage message : messages) {
            MutationProcessingResult result = pipeline.process(message, updatedTime);
            accumulator.append(result);
            if (accumulator.shouldFlush()) {
                flushedBatches.add(flush(accumulator.isFlushIntervalReached()
                        ? BatchFlushReason.FLUSH_INTERVAL_REACHED
                        : BatchFlushReason.BATCH_SIZE_REACHED));
            }
        }
        return flushedBatches;
    }

    public MutationBatchAccumulator.MutationBatch flush(BatchFlushReason reason) {
        if (accumulator.isEmpty()) {
            return new MutationBatchAccumulator.MutationBatch(reason, List.of(), List.of());
        }
        MutationBatchAccumulator.MutationBatch batch = accumulator.drain(reason);
        publisher.publishBatch(batch.getSnapshots(), batch.getOffsets());
        return batch;
    }
}
