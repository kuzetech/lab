package com.funnydb.mutation.service;

import com.funnydb.mutation.model.ConsumedMutationMessage;
import com.funnydb.mutation.model.ProducedSnapshotMessage;
import com.funnydb.mutation.model.TopicPartitionOffset;

public class MutationProcessingResult {
    private final ConsumedMutationMessage sourceMessage;
    private final MutationEnvelope envelope;
    private final ProducedSnapshotMessage snapshotMessage;
    private final TopicPartitionOffset offsetToCommit;

    public MutationProcessingResult(ConsumedMutationMessage sourceMessage,
                                    MutationEnvelope envelope,
                                    ProducedSnapshotMessage snapshotMessage,
                                    TopicPartitionOffset offsetToCommit) {
        this.sourceMessage = sourceMessage;
        this.envelope = envelope;
        this.snapshotMessage = snapshotMessage;
        this.offsetToCommit = offsetToCommit;
    }

    public ConsumedMutationMessage getSourceMessage() {
        return sourceMessage;
    }

    public MutationEnvelope getEnvelope() {
        return envelope;
    }

    public ProducedSnapshotMessage getSnapshotMessage() {
        return snapshotMessage;
    }

    public TopicPartitionOffset getOffsetToCommit() {
        return offsetToCommit;
    }
}
