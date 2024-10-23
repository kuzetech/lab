package com.xmfunny.funnydb.flink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.xmfunny.funnydb.flink.util.ObjectMapperInstance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.xmfunny.funnydb.flink.Constant.*;

/**
 * 设备信息缓存数据
 *
 * @author wulh
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 当从埋点事件转换到设备信息数据时忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 序列化时忽略空值输出
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceInfoCacheData implements Serializable {
    /**
     * IP 地理位置ID
     */
    @JsonProperty(IP_GEONAME_ID_KEY)
    private Integer ipGeonameId;
    /**
     * IP 大洲
     */
    @JsonProperty(IP_CONTINENT_KEY)
    private String ipContinent;
    /**
     * IP 国家
     */
    @JsonProperty(IP_COUNTRY_KEY)
    private String ipCountry;
    /**
     * IP 省份
     */
    @JsonProperty(IP_PROVINCE_KEY)
    private String ipProvince;
    /**
     * IP 城市
     */
    @JsonProperty(IP_CITY_KEY)
    private String ipCity;
    /**
     * OS平台
     */
    @JsonProperty(OS_PLATFORM_KEY)
    private String osPlatform;
    /**
     * 设备型号
     */
    @JsonProperty(DEVICE_MODEL_KEY)
    private String deviceModel;
    /**
     * 渠道
     */
    @JsonProperty(CHANNEL_KEY)
    private String channel;
    /**
     * 时区偏移, 弃用,变更为浮点数
     */
    @Deprecated
    private Integer zoneOffset;
    /**
     * 时区偏移
     */
    @JsonProperty(ZONE_OFFSET_KEY)
    private Float zoneOffsetV2;

    /**
     * 转换到Payload数据
     * 由于所需要缓存的字段均在 payload 内,所以这里仅需要将缓存的字段转换成 Map 类型.
     * 若后续增加预置字段缓存, 需调整转换方案.
     *
     * @return payload数据
     */
    public Map<String, Object> toEventPayload() {
        final Map<String, Object> payload = Maps.newHashMap();
        this.putEventPayload(payload, IP_GEONAME_ID_KEY, ipGeonameId);
        this.putEventPayload(payload, IP_CONTINENT_KEY, ipContinent);
        this.putEventPayload(payload, IP_COUNTRY_KEY, ipCountry);
        this.putEventPayload(payload, IP_PROVINCE_KEY, ipProvince);
        this.putEventPayload(payload, IP_CITY_KEY, ipCity);
        this.putEventPayload(payload, OS_PLATFORM_KEY, osPlatform);
        this.putEventPayload(payload, DEVICE_MODEL_KEY, deviceModel);
        this.putEventPayload(payload, CHANNEL_KEY, channel);
        // 时区字段保持兼容, 读取时优先读取 浮点数字段, 若未读取到则读取整型字段, 并转换成浮点数.
        this.putEventPayload(payload, ZONE_OFFSET_KEY,
                Optional.ofNullable(zoneOffsetV2)
                        .orElseGet(() -> Optional.ofNullable(zoneOffset)
                                .map(Integer::floatValue)
                                .orElse(null)
                        )
        );

        return payload;
    }

    private void putEventPayload(Map<String, Object> payload, String key, Object value) {
        if (Objects.isNull(payload) || Objects.isNull(value)) {
            return;
        }

        payload.put(key, value);
    }

    /**
     * 通过埋点数据转换成设备状态数据
     * 由于所需要缓存的字段均在 payload 内, 这里直接通过JSON转换出对应实体(需要忽略未知字段).
     * 若后续增加预置字段缓存, 需调整转换方案.
     *
     * @param event 埋点事件
     * @return 设备状态数据
     */
    public static DeviceInfoCacheData of(TrackEvent event) {
        Objects.requireNonNull(event, "track event is null");
        Map<String, Object> payload = event.getPayload();
        if (Objects.isNull(payload)) {
            return new DeviceInfoCacheData();
        }
        ObjectMapper mapper = ObjectMapperInstance.getInstance();
        return mapper.convertValue(payload, DeviceInfoCacheData.class);
    }
}
