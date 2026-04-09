package com.funnydb.mutation.model;

import java.util.Objects;

public class TopicPartitionOffset {
    private final String topic;
    private final int partition;
    private final long nextOffset;

    public TopicPartitionOffset(String topic, int partition, long nextOffset) {
        this.topic = topic;
        this.partition = partition;
        this.nextOffset = nextOffset;
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public long getNextOffset() {
        return nextOffset;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TopicPartitionOffset)) {
            return false;
        }
        TopicPartitionOffset that = (TopicPartitionOffset) other;
        return partition == that.partition && nextOffset == that.nextOffset
                && Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, partition, nextOffset);
    }
}
