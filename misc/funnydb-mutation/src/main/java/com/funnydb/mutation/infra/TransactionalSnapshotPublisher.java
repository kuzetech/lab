package com.funnydb.mutation.infra;

import com.funnydb.mutation.model.ProducedSnapshotMessage;
import com.funnydb.mutation.model.TopicPartitionOffset;

import java.util.List;

public interface TransactionalSnapshotPublisher {
    void publishBatch(List<ProducedSnapshotMessage> snapshots, List<TopicPartitionOffset> offsets);
}
