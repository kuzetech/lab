package com.funnydb.mutation.service;

import com.funnydb.mutation.infra.RedisMutationStore;
import com.funnydb.mutation.infra.TransactionalSnapshotPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BooleanSupplier;


public class MutationWorkerLauncher {
    private static final Logger LOG = LoggerFactory.getLogger(MutationWorkerLauncher.class);

    private final int workerCount;
    private final MutationEngine engine;
    private final RedisMutationStore store;
    private final TransactionalSnapshotPublisher publisher;
    private final int batchSize;
    private final long flushIntervalMs;
    private final Clock clock;
    private final MutationMetrics metrics;
    private final WorkerFactory workerFactory;

    public MutationWorkerLauncher(int workerCount,
                                  MutationEngine engine,
                                  RedisMutationStore store,
                                  TransactionalSnapshotPublisher publisher,
                                  int batchSize,
                                  long flushIntervalMs,
                                  Clock clock,
                                  MutationMetrics metrics,
                                  WorkerFactory workerFactory) {
        this.workerCount = workerCount;
        this.engine = engine;
        this.store = store;
        this.publisher = publisher;
        this.batchSize = batchSize;
        this.flushIntervalMs = flushIntervalMs;
        this.clock = clock;
        this.metrics = metrics;
        this.workerFactory = workerFactory;
    }

    public WorkerLaunchSummary runUntilStopped(BooleanSupplier keepRunning) {
        ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        try {
            List<Future<MutationWorker.WorkerRunResult>> futures = new ArrayList<>();
            for (int i = 0; i < workerCount; i++) {
                LOG.info("Starting mutation worker {}", i);
                futures.add(executor.submit(newWorkerTask(i, keepRunning)));
            }
            return collect(futures);
        } finally {
            executor.shutdownNow();
        }
    }

    private Callable<MutationWorker.WorkerRunResult> newWorkerTask(int workerIndex, BooleanSupplier keepRunning) {
        return () -> {
            MutationCoordinator coordinator = new MutationCoordinator(
                    new MutationPipeline(engine, store, metrics),
                    publisher,
                    batchSize,
                    flushIntervalMs,
                    clock,
                    metrics
            );
            MutationWorker worker = workerFactory.create(workerIndex, coordinator, metrics);
            return worker.runUntilStopped(keepRunning);
        };
    }

    private WorkerLaunchSummary collect(List<Future<MutationWorker.WorkerRunResult>> futures) {
        int processed = 0;
        int invalid = 0;
        int flushedBatches = 0;
        for (Future<MutationWorker.WorkerRunResult> future : futures) {
            try {
                MutationWorker.WorkerRunResult result = future.get();
                processed += result.getProcessedCount();
                invalid += result.getInvalidCount();
                flushedBatches += result.getFlushedBatches().size();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for workers", exception);
            } catch (ExecutionException exception) {
                throw new IllegalStateException("Worker failed", exception.getCause());
            }
        }
        return new WorkerLaunchSummary(processed, invalid, flushedBatches);
    }

    public interface WorkerFactory {
        MutationWorker create(int workerIndex, MutationCoordinator coordinator, MutationMetrics metrics);
    }

    public static class WorkerLaunchSummary {
        private final int processedCount;
        private final int invalidCount;
        private final int flushedBatchCount;

        public WorkerLaunchSummary(int processedCount, int invalidCount, int flushedBatchCount) {
            this.processedCount = processedCount;
            this.invalidCount = invalidCount;
            this.flushedBatchCount = flushedBatchCount;
        }

        public int getProcessedCount() {
            return processedCount;
        }

        public int getInvalidCount() {
            return invalidCount;
        }

        public int getFlushedBatchCount() {
            return flushedBatchCount;
        }
    }
}
