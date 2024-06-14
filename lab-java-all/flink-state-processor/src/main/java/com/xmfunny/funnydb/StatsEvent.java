package com.xmfunny.funnydb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class StatsEvent implements Serializable {
    @JsonProperty("since_time")
    private Long sinceTime;
    @JsonProperty("until_time")
    private Long untilTime;
    @JsonIgnore
    private String outputTopic;
    private String event;
    @JsonProperty("success_count")
    private Integer successCount;
    @JsonProperty("partial_success_count")
    private Integer partialSuccessCount;
    @JsonProperty("failed_count")
    private Integer failedCount;

    public StatsEvent() {
    }

    public StatsEvent(String type, String event, String outputTopic) {
        this.failedCount = 0;
        this.successCount = 0;
        this.partialSuccessCount = 0;
        this.outputTopic = outputTopic;
        if (type.equalsIgnoreCase("UserMutation")) {
            this.event = "__user__";
        } else if (type.equalsIgnoreCase("DeviceMutation")) {
            this.event = "__device__";
        } else {
            this.event = event;
        }
    }

    public void increaseSuccessCount() {
        this.successCount += 1;
    }

    public void increaseFailedCount() {
        this.failedCount += 1;
    }

    public void increasePartialSuccessCount() {
        this.partialSuccessCount += 1;
        this.successCount += 1;
    }
}
