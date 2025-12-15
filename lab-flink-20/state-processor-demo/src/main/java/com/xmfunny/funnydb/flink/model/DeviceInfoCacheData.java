package com.xmfunny.funnydb.flink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // 显式指定参与比较的字段
public class DeviceInfoCacheData implements Serializable {
    /**
     * IP 客户端 IP 地址
     */
    @EqualsAndHashCode.Include // 关键标识字段
    @JsonProperty(IP_KEY)
    private String ip;
    /**
     * IP 地理位置ID
     */
    @EqualsAndHashCode.Include // 关键标识字段
    @JsonProperty(IP_GEONAME_ID_KEY)
    private Integer ipGeonameId;
    /**
     * IP 大洲
     */
    @EqualsAndHashCode.Include // 关键标识字段
    @JsonProperty(IP_CONTINENT_KEY)
    private String ipContinent;
    /**
     * IP 国家
     */
    @EqualsAndHashCode.Include // 关键标识字段
    @JsonProperty(IP_COUNTRY_KEY)
    private String ipCountry;
    /**
     * IP 省份
     */
    @EqualsAndHashCode.Include // 关键标识字段
    @JsonProperty(IP_PROVINCE_KEY)
    private String ipProvince;
    /**
     * IP 城市
     */
    @EqualsAndHashCode.Include // 关键标识字段
    @JsonProperty(IP_CITY_KEY)
    private String ipCity;
    /**
     * OS平台
     */
    @EqualsAndHashCode.Include // 关键标识字段
    @JsonProperty(OS_PLATFORM_KEY)
    private String osPlatform;
    /**
     * 设备型号
     */
    @EqualsAndHashCode.Include // 关键标识字段
    @JsonProperty(DEVICE_MODEL_KEY)
    private String deviceModel;
    /**
     * 渠道
     */
    @EqualsAndHashCode.Include // 关键标识字段
    @JsonProperty(CHANNEL_KEY)
    private String channel;
    /**
     * 时区偏移, 弃用,变更为浮点数
     * 不参与比较的字段
     */
    @Deprecated
    private Integer zoneOffset;
    /**
     * 时区偏移
     */
    @EqualsAndHashCode.Include // 关键标识字段
    @JsonProperty(ZONE_OFFSET_KEY)
    private Float zoneOffsetV2;

}
