package com.funnydb.mutation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

public class MutationData {
    @JsonProperty("#operate")
    private String operate;

    @JsonProperty("#time")
    private Long time;

    @JsonProperty("#identify")
    private String identify;

    @JsonProperty("#data_lifecycle")
    private String dataLifecycle;

    @JsonProperty("properties")
    private Map<String, Object> properties = new LinkedHashMap<>();

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getDataLifecycle() {
        return dataLifecycle;
    }

    public void setDataLifecycle(String dataLifecycle) {
        this.dataLifecycle = dataLifecycle;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
