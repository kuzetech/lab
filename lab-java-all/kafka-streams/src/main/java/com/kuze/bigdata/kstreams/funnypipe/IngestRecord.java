package com.kuze.bigdata.kstreams.funnypipe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@JsonIgnoreProperties(value = {"logId", "key"})
public class IngestRecord {

    public static Set<String> ExtractMetadataFieldWhiteList = new HashSet<>();
    public static Set<String> IngestRecordTypeWhiteList = new HashSet<>();

    static {
        ExtractMetadataFieldWhiteList.add("ingestTime");
        IngestRecordTypeWhiteList.add("event");
        IngestRecordTypeWhiteList.add("usermutation");
        IngestRecordTypeWhiteList.add("devicemutation");
    }

    private static final Random r = new Random();

    private String type;
    private ObjectNode data;
    @JsonProperty("ingest_time")
    private Long ingestTime;
    private String ip;
    private String app;
    @JsonIgnore
    private String event;
    @JsonProperty("access_id")
    private String accessId;

    public String getKey() {
        return this.app + this.getLogId();
    }

    public byte[] getKafkaMessageKey(String[] keyFields) {
        if (keyFields == null || keyFields.length == 0) {
            return null;
        }

        String[] keyArray = new String[keyFields.length];
        for (int j = 0; j < keyFields.length; j++) {
            JsonNode node = data.get(keyFields[j]);
            if (node == null) {
                keyArray[j] = null;
            } else {
                keyArray[j] = node.asText();
            }
        }
        return Arrays.toString(keyArray).getBytes(StandardCharsets.UTF_8);
    }


    public IngestRecord cloneExceptData() {
        IngestRecord clone = new IngestRecord();
        clone.setType(this.type);
        clone.setApp(this.app);
        clone.setIp(this.ip);
        clone.setIngestTime(this.ingestTime);
        clone.setAccessId(this.accessId);
        return clone;
    }

    public String getLogId() {
        JsonNode node = this.data.get("#log_id");
        if (node == null) {
            return "";
        }
        return node.asText();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ObjectNode getData() {
        return data;
    }

    public void setData(ObjectNode data) {
        this.data = data;
    }

    public Long getIngestTime() {
        return ingestTime;
    }

    public void setIngestTime(Long ingestTime) {
        this.ingestTime = ingestTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}