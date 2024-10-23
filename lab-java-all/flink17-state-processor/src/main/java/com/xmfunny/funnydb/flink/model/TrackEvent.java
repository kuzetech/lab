package com.xmfunny.funnydb.flink.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xmfunny.funnydb.flink.Constant;
import com.xmfunny.funnydb.flink.util.DataTypeUtils;
import lombok.*;

import java.util.Map;
import java.util.Objects;

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
    private String dataLifecycle;
    /**
     * 日志ID
     */
    @JsonProperty(LOG_ID_KEY)
    private String logId;
    /**
     * 设备ID
     */
    @JsonProperty(DEVICE_ID_KEY)
    private String deviceId;
    /**
     * 用户ID
     */
    @JsonProperty(USER_ID_KEY)
    private String userId;
    /**
     * 事件名称
     */
    @JsonProperty(EVENT_NAME_KEY)
    private String event;
    /**
     * Ingest 处理的时间戳(毫秒级)
     */
    @JsonProperty(INGEST_TIME_KEY)
    private long ingestTime;
    /**
     * 事件发生时间戳(毫秒级)
     */
    @JsonProperty(LOG_TIME_KEY)
    private long time;

    /**
     * 事件操作符
     */
    @JsonProperty(OP_KEY)
    private int op;

    /**
     * 额外携带的数据
     */
    @JsonAnySetter
    private Map<String, Object> payload;

    @JsonAnyGetter()
    public Map<String, Object> getPayload() {
        return this.payload;
    }

    /**
     * 根据字段名称提取值
     *
     * @param fieldName 字段名称
     * @return 值
     */
    public Object getValue(final String fieldName) {
        EventValueExtractor<TrackEvent, ?> valueExtractor = Constant.TRACK_EVENT_FIXED_FIELD_EXTRACTOR_MAPPING.get(fieldName);
        if (Objects.nonNull(valueExtractor)) {
            return valueExtractor.apply(this);
        }
        if (Objects.isNull(payload)) {
            return null;
        }
        return payload.get(fieldName);
    }

    public String getAsString(final String fieldName) {
        Object value = getValue(fieldName);
        if (value == null) {
            return null;
        }

        return DataTypeUtils.toString(value);
    }

    public Boolean getAsBoolean(final String fieldName) {
        return DataTypeUtils.toBoolean(getValue(fieldName), fieldName);
    }

    public Integer getAsInt(final String fieldName) {
        return DataTypeUtils.toInteger(getValue(fieldName), fieldName);
    }

    public Long getAsLong(final String fieldName) {
        return DataTypeUtils.toLong(getValue(fieldName), fieldName);
    }

    public Float getAsFloat(final String fieldName) {
        return DataTypeUtils.toFloat(getValue(fieldName), fieldName);
    }

    public Double getAsDouble(final String fieldName) {
        return DataTypeUtils.toDouble(getValue(fieldName), fieldName);
    }
}
