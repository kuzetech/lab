package com.funnydb.mutation.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProducedSnapshotMessage {
    private final String topic;
    private final String key;
    private final Map<String, Object> snapshot;

    public ProducedSnapshotMessage(String topic, String key, Map<String, Object> snapshot) {
        this.topic = topic;
        this.key = key;
        this.snapshot = new LinkedHashMap<>(snapshot);
    }

    public String getTopic() {
        return topic;
    }

    public String getKey() {
        return key;
    }

    public Map<String, Object> getSnapshot() {
        return snapshot;
    }
}
