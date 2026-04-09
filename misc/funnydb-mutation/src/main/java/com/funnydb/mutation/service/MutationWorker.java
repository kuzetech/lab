package com.funnydb.mutation.service;

import com.funnydb.mutation.infra.MutationConsumer;
import com.funnydb.mutation.model.ConsumedMutationMessage;
import com.funnydb.mutation.model.MutationPollBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class MutationWorker {
    private static final Logger LOG = LoggerFactory.getLogger(MutationWorker.class);

    private final MutationConsumer consumer;
    private final MutationCoordinator coordinator;
    private final Clock clock;
    private final MutationMetrics metrics;

    public MutationWorker(MutationConsumer consumer, MutationCoordinator coordinator, Clock clock, MutationMetrics metrics) {
        this.consumer = consumer;
        this.coordinator = coordinator;
        this.clock = clock;
        this.metrics = metrics;
    }

    public WorkerRunResult runUntilDrained() {
        return doRun(() -> true, true);
    }

    public WorkerRunResult runUntilStopped(BooleanSupplier keepRunning) {
        return doRun(keepRunning, false);
    }

    private WorkerRunResult doRun(BooleanSupplier keepRunning, boolean stopWhenEmpty) {
        int processed = 0;
        int invalid = 0;
        List<MutationBatchAccumulator.MutationBatch> flushedBatches = new ArrayList<>();
        try {
            while (keepRunning.getAsBoolean()) {
                MutationPollBatch pollBatch = consumer.poll();
                List<ConsumedMutationMessage> messages = pollBatch.getMessages();
                invalid += pollBatch.getInvalidCount();
                if (pollBatch.isEmpty() && stopWhenEmpty) {
                    break;
                }
                if (!messages.isEmpty()) {
                    LOG.info("Processing {} mutation messages", messages.size());
                    processed += messages.size();
                    metrics.recordProcessedMessages(messages.size());
                    flushedBatches.addAll(coordinator.processMessages(messages, clock.millis()));
                }
            }
            MutationBatchAccumulator.MutationBatch finalBatch = coordinator.flush(BatchFlushReason.SHUTDOWN);
            if (!finalBatch.getSnapshots().isEmpty() || !finalBatch.getOffsets().isEmpty()) {
                flushedBatches.add(finalBatch);
            }
            if (invalid > 0) {
                metrics.recordInvalidMessages(invalid);
            }
            return new WorkerRunResult(processed, invalid, flushedBatches);
        } finally {
            consumer.close();
        }
    }

    public static class WorkerRunResult {
        private final int processedCount;
        private final int invalidCount;
        private final List<MutationBatchAccumulator.MutationBatch> flushedBatches;

        public WorkerRunResult(int processedCount,
                               int invalidCount,
                               List<MutationBatchAccumulator.MutationBatch> flushedBatches) {
            this.processedCount = processedCount;
            this.invalidCount = invalidCount;
            this.flushedBatches = flushedBatches;
        }

        public int getProcessedCount() {
            return processedCount;
        }

        public int getInvalidCount() {
            return invalidCount;
        }

        public List<MutationBatchAccumulator.MutationBatch> getFlushedBatches() {
            return flushedBatches;
        }
    }
}
