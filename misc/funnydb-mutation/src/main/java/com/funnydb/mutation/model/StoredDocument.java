package com.funnydb.mutation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

public class StoredDocument {
    private Map<String, Object> data = new LinkedHashMap<>();
    private Metadata metadata = new Metadata();

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data == null ? new LinkedHashMap<>() : data;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata == null ? new Metadata() : metadata;
    }

    public StoredDocument copy() {
        StoredDocument copied = new StoredDocument();
        copied.getData().putAll(data);
        copied.getMetadata().setLastEventTime(metadata.getLastEventTime());
        copied.getMetadata().setCreatedTime(metadata.getCreatedTime());
        copied.getMetadata().setDataLifecycle(metadata.getDataLifecycle());
        return copied;
    }

    public static class Metadata {
        @JsonProperty("last_event_time")
        private Long lastEventTime;

        @JsonProperty("#created_time")
        private Long createdTime;

        @JsonProperty("#data_lifecycle")
        private String dataLifecycle;

        @JsonProperty("last_event_time")
        public Long getLastEventTime() {
            return lastEventTime;
        }

        @JsonProperty("last_event_time")
        public void setLastEventTime(Long lastEventTime) {
            this.lastEventTime = lastEventTime;
        }

        @JsonProperty("#created_time")
        public Long getCreatedTime() {
            return createdTime;
        }

        @JsonProperty("#created_time")
        public void setCreatedTime(Long createdTime) {
            this.createdTime = createdTime;
        }

        @JsonProperty("#data_lifecycle")
        public String getDataLifecycle() {
            return dataLifecycle;
        }

        @JsonProperty("#data_lifecycle")
        public void setDataLifecycle(String dataLifecycle) {
            this.dataLifecycle = dataLifecycle;
        }
    }
}
