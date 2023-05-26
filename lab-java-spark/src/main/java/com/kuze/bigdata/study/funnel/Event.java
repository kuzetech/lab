package com.kuze.bigdata.study.funnel;

import java.io.Serializable;

public class Event implements Serializable {
    private String deviceId;
    private Long time;
    private Integer step;

    public Event() {
    }

    public Event(String deviceId, Long time, Integer step) {
        this.deviceId = deviceId;
        this.time = time;
        this.step = step;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }
}

