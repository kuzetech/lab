package com.kuze.bigdata.kstreams.funnypipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kuze.bigdata.kstreams.funnypipe.utils.JsonUtil;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@JsonIgnoreProperties(value = {"distinctKey"})
public class EnrichedEvent {

    public static final String EVENT_TYPE_EVENT = "Event";
    public static final String EVENT_TYPE_USER_MUTATION = "UserMutation";
    public static final String EVENT_TYPE_DEVICE_MUTATION = "DeviceMutation";

    private IngestRecord data;
    private String logId;
    private String app;
    // 这里的 event 仅用于去重，可能是实际的事件名也可能是 UserMutation 或 DeviceMutation
    private String event;

    public EnrichedEvent() {
    }

    public EnrichedEvent(Headers headers, byte[] data) {
        try {
            this.data = JsonUtil.mapper.readValue(data, IngestRecord.class);
        } catch (IOException e) {
            this.data = null;
        }

        Header logId = headers.lastHeader("log_id");
        if (logId == null) {
            // keyby 的键不能为空，所以返回空字符串
            this.logId = "";
        } else {
            this.logId = new String(logId.value(), StandardCharsets.UTF_8);
        }

        Header app = headers.lastHeader("app");
        if (app == null) {
            // keyby 的键不能为空，所以返回空字符串
            this.app = "";
        } else {
            this.app = new String(app.value(), StandardCharsets.UTF_8);
        }

        Header event = headers.lastHeader("event");
        if (event == null) {
            // keyby 的键不能为空，所以返回空字符串
            this.event = "";
        } else {
            this.event = new String(event.value(), StandardCharsets.UTF_8);
        }
    }

    public IngestRecord getData() {
        return data;
    }

    public void setData(IngestRecord data) {
        this.data = data;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDistinctKey() {
        return this.app + this.event + this.logId;
    }


}
