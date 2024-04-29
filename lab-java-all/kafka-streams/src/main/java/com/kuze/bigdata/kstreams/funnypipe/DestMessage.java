package com.kuze.bigdata.kstreams.funnypipe;

public class DestMessage {
    private String topic;
    private byte[] value;

    public DestMessage(String topic, byte[] value) {
        this.topic = topic;
        this.value = value;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
