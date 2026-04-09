package com.funnydb.mutation.model;

public class ConsumedMutationMessage {
    private final String topic;
    private final int partition;
    private final long offset;
    private final String key;
    private final MutationEvent event;

    public ConsumedMutationMessage(String topic, int partition, long offset, String key, MutationEvent event) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.key = key;
        this.event = event;
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public String getKey() {
        return key;
    }

    public MutationEvent getEvent() {
        return event;
    }
}
