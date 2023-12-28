package com.kuzetech.bigdata.spark.sql;

import java.io.Serializable;

public class Event implements Serializable {

    private String uid;
    private String eventId;
    private String eventTime;

    public Event(String uid, String eventId, String eventTime) {
        this.uid = uid;
        this.eventId = eventId;
        this.eventTime = eventTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
