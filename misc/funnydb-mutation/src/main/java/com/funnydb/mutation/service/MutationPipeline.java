package com.funnydb.mutation.service;

import com.funnydb.mutation.infra.RedisMutationStore;
import com.funnydb.mutation.model.ConsumedMutationMessage;
import com.funnydb.mutation.model.MutationEvent;
import com.funnydb.mutation.model.ProducedSnapshotMessage;
import com.funnydb.mutation.model.StoredDocument;
import com.funnydb.mutation.model.TopicPartitionOffset;

public class MutationPipeline {
    private final MutationEngine engine;
    private final RedisMutationStore redisMutationStore;
    private final MutationMetrics metrics;

    public MutationPipeline(MutationEngine engine, RedisMutationStore redisMutationStore, MutationMetrics metrics) {
        this.engine = engine;
        this.redisMutationStore = redisMutationStore;
        this.metrics = metrics;
    }

    public MutationProcessingResult process(ConsumedMutationMessage message, long updatedTime) {
        long startNanos = System.nanoTime();
        MutationEvent event = message.getEvent();
        MutationRouting routing = engine.buildRouting(event);
        MutationApplyResult result = redisMutationStore.apply(routing.getRedisKey(), event);
        metrics.recordMutationStatus(result.getStatus());
        StoredDocument document = result.getDocument();
        MutationEnvelope envelope = new MutationEnvelope(
                routing,
                result,
                engine.buildSnapshot(event, document, updatedTime)
        );
        ProducedSnapshotMessage snapshotMessage = new ProducedSnapshotMessage(
                routing.getOutputTopic(),
                routing.getOutputKey(),
                envelope.getSnapshot()
        );
        TopicPartitionOffset offsetToCommit = new TopicPartitionOffset(
                message.getTopic(),
                message.getPartition(),
                message.getOffset() + 1
        );
        MutationProcessingResult processingResult = new MutationProcessingResult(message, envelope, snapshotMessage, offsetToCommit);
        metrics.recordMessageProcessing(System.nanoTime() - startNanos);
        return processingResult;
    }
}
