package com.kuzetech.bigdata.flink.funny;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FunnyMessage implements Serializable {
    public static final String CHANNEL_KEY_KAFKA = "kafka";
    public static final String CHANNEL_KEY_PULSAR = "pulsar";

    private String channel;
    private String app;
    private String event;
    private String logId;
    private Long ingestTime;

    public String getKey() {
        return this.app + "@" + this.event;
    }

}
