package com.kuzetech.bigdata.study.funnel;

import java.io.Serializable;

public class FunnyEvent implements Serializable {

    private String deviceId;
    private Long time;
    private String event;
    private Integer step;

    public FunnyEvent() {
    }

    public FunnyEvent(String deviceId, Long time, String event, Integer step) {
        this.deviceId = deviceId;
        this.time = time;
        this.event = event;
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

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getKey(){
        return this.event + "-" + this.step;
    }
}
