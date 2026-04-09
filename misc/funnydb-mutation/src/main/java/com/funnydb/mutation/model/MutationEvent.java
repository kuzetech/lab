package com.funnydb.mutation.model;

public class MutationEvent {
    private String app;
    private String type;
    private MutationData data;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MutationData getData() {
        return data;
    }

    public void setData(MutationData data) {
        this.data = data;
    }
}
