package com.xmfunny.funnydb.flink.pipeline.validator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class StatsEvent implements Serializable {
    public static final String EVENT_NAME_OF_USER_MUTATION_TYPE = "__user__";
    public static final String EVENT_NAME_OF_DEVICE_MUTATION_TYPE = "__device__";

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

    public StatsEvent(String event, String outputTopic) {
        this.failedCount = 0;
        this.successCount = 0;
        this.partialSuccessCount = 0;
        this.outputTopic = outputTopic;
        this.event = event;
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
