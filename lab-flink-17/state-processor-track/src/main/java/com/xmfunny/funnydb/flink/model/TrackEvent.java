package com.xmfunny.funnydb.flink.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

import static com.xmfunny.funnydb.flink.Constant.*;

/**
 * 事件
 *
 * @author wulh
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TrackEvent implements Event {
    /**
     * 环境
     */
    @JsonProperty(DATA_LIFECYCLE_KEY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JSONField(name = DATA_LIFECYCLE_KEY)
    private String dataLifecycle;
    /**
     * 日志ID
     */
    @JsonProperty(LOG_ID_KEY)
    @JSONField(name = LOG_ID_KEY)
    private String logId;
    /**
     * 设备ID
     */
    @JsonProperty(DEVICE_ID_KEY)
    @JSONField(name = DEVICE_ID_KEY)
    private String deviceId;
    /**
     * 用户ID
     */
    @JsonProperty(USER_ID_KEY)
    @JSONField(name = USER_ID_KEY)
    private String userId;
    /**
     * 事件名称
     */
    @JsonProperty(EVENT_NAME_KEY)
    @JSONField(name = EVENT_NAME_KEY)
    private String event;
    /**
     * Ingest 处理的时间戳(毫秒级)
     */
    @JsonProperty(INGEST_TIME_KEY)
    @JSONField(name = INGEST_TIME_KEY)
    private long ingestTime;
    /**
     * 事件发生时间戳(毫秒级)
     */
    @JsonProperty(LOG_TIME_KEY)
    @JSONField(name = LOG_TIME_KEY)
    private long time;

    /**
     * 额外携带的数据
     */
    @JsonAnySetter
    @JSONField(unwrapped = true)
    private Map<String, Object> payload = new HashMap<>();

    @JsonAnyGetter()
    public Map<String, Object> getPayload() {
        return this.payload;
    }

}
