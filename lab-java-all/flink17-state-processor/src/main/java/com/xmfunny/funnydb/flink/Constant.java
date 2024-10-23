package com.xmfunny.funnydb.flink;

import com.xmfunny.funnydb.flink.model.EventValueExtractor;
import com.xmfunny.funnydb.flink.model.TrackEvent;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * 常量定义
 * 以下大部分基于FunnyDB项目约定共识,不做修改.
 *
 * @author wulh
 */
public class Constant implements Serializable {

    public static final String METRICS_GROUP_FUNNYDB = "funnydb";

    public static final String METRICS_EVENT_FILTER = "eventFilter";

    /**
     * 数据周期KEY
     */
    public static final String DATA_LIFECYCLE_KEY = "#data_lifecycle";

    /**
     * 用户ID KEY
     */
    public static final String USER_ID_KEY = "#user_id";

    /**
     * 设备ID KEY
     */
    public static final String DEVICE_ID_KEY = "#device_id";

    /**
     * 事件名称KEY
     */
    public static final String EVENT_NAME_KEY = "#event";

    /**
     * 标识符KEY
     */
    public static final String IDENTIFY_KEY = "#identify";

    /**
     * 标识符类型
     */
    public static final String IDENTIFY_TYPE_KEY = "#identify_type";

    /**
     * 操作
     */
    public static final String OPERATE_KEY = "#operate";

    /**
     * Ingest 接收时间KEY
     */
    public static final String INGEST_TIME_KEY = "#ingest_time";

    /**
     * 事件日志ID
     */
    public static final String LOG_ID_KEY = "#log_id";

    /**
     * 日志记录时间KEY
     */
    public static final String LOG_TIME_KEY = "#time";

    /**
     * 日志 sdk type KEY
     */
    public static final String LOG_SDK_TYPE_KEY = "#sdk_type";

    /**
     * 日志 sdk type KEY
     */
    public static final String LOG_SDK_VERSION_KEY = "#sdk_version";

    /**
     * 事件操作KEY
     */
    public static final String OP_KEY = "#op";
    /**
     * 时区偏移 KEY
     */
    public static final String ZONE_OFFSET_KEY = "#zone_offset";
    /**
     * IP 大洲 KEY
     */
    public static final String IP_CONTINENT_KEY = "#ip_continent";
    /**
     * IP 国家 KEY
     */
    public static final String IP_COUNTRY_KEY = "#ip_country";
    /**
     * IP 省份 KEY
     */
    public static final String IP_PROVINCE_KEY = "#ip_province";
    /**
     * IP 城市 KEY
     */
    public static final String IP_CITY_KEY = "#ip_city";
    /**
     * IP 地理位置ID
     */
    public static final String IP_GEONAME_ID_KEY = "#ip_geoname_id";

    /**
     * OS平台 KEY
     */
    public static final String OS_PLATFORM_KEY = "#os_platform";
    /**
     * 设备模型 KEY
     */
    public static final String DEVICE_MODEL_KEY = "#device_model";

    /**
     * 渠道 KEY
     */
    public static final String CHANNEL_KEY = "#channel";

    /**
     * 服务器ID KEY
     */
    public static final String SERVER_ID_KEY = "#server_id";

    /**
     * 账号ID KEY
     */
    public static final String ACCOUNT_ID_KEY = "#account_id";

    /**
     * 时长 KEY
     */
    public static final String DURATION_KEY = "#duration";
    /**
     * 用户数 KEY
     */
    public static final String USER_COUNT_KEY = "#user_count";

    /**
     * 变更事件时间KEY
     */
    public static final String MUTATION_EVENT_TIME_KEY = "#event_time";

    /**
     * 创建时间戳KEY
     */
    public static final String CREATED_TIME_KEY = "#created_time";

    /**
     * 更新时间戳KEY
     */
    public static final String UPDATED_TIME_KEY = "#updated_time";

    /**
     * 首次登陆时间KEY
     */
    public static final String FIRST_LOGIN_TIME_KEY = "#first_login_time";

    /**
     * 接入的SDK类型
     * e.g Unity/Java/Swift/ingest-client
     */
    public static final String SDK_TYPE_KEY = "#sdk_type";

    /**
     * IngestClient 事件标识(目前直接写死,后续可根据规则进行调整)
     */
    public static final String INGEST_CLIENT_NAME = "ingest-client";

    /*
    Flink作业相关常量
     */
    /**
     * 默认Token ttl存活时间
     */
    public static final String DEFAULT_TOKEN_TTL = "30m";

    /**
     * 默认保活窗口大小
     */
    public static final String DEFAULT_KEEP_ALIVE_WINDOW = "1h";

    /**
     * 变更事件延迟窗口大小
     */
    public static final String DEFAULT_MUTATION_WINDOW = "5m";

    /**
     * 水印策略空闲超时
     * 如果数据源中的某一个分区/分片在一段时间内未发送事件数据，则意味着 WatermarkGenerator 也不会获得任何新数据去生成 watermark。
     * 我们称这类数据源为空闲输入或空闲源。在这种情况下，当某些其他分区仍然发送事件数据的时候就会出现问题。
     * 由于下游算子 watermark 的计算方式是取所有不同的上游并行数据源 watermark 的最小值，则其 watermark 将不会发生变化。
     */
    public static final Duration DEFAULT_WATERMARK_STRATEGY_IDLENESS_TIMEOUT = Duration.ofMinutes(1);

    /**
     * 水印策略,乱序数据延迟时长
     */
    public static final Duration DEFAULT_WATERMARK_STRATEGY_MAX_OUT_OF_ORDERNESS = Duration.ofSeconds(30);

    /*
    Kafka相关常量模板
     */
    /**
     * Kafka消费者组ID模板
     */
    public static final String KAFKA_GROUP_ID_TEMP = "flink-group-%s";

    /**
     * Kafka消费者组ID模板
     */
    public static final String KAFKA_CLIENT_ID_TEMP = "flink-client-%s";

    /**
     * 默认Kafka消费服务器
     */
    public static final String DEFAULT_KAFKA_BOOTSTRAP_SERVERS = "kafka:9092";

    /**
     * 服务自动发现间隔时长(暂定1分钟)
     */
    public static final long DEFAULT_PARTITION_DISCOVERY_INTERVAL_MS_VALUE = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);

    /**
     * 调整数据生成大小上限(默认为10MB)
     */
    public static final long DEFAULT_MAX_REQUEST_SIZE = 10485760;

    /**
     * 默认Kafka 创建Topic的分区数
     */
    public static final Integer DEFAULT_KAFKA_CREATE_TOPIC_PARTITIONS = 9;

    /**
     * 默认Kafka 创建Topic的副本数
     */
    public static final Integer DEFAULT_KAFKA_CREATE_TOPIC_REPLICATION = 1;

    /**
     * 默认Kafka消费者数量
     */
    public static final Integer DEFAULT_INPUT_PARALLELISM = 1;

    /**
     * 默认输入并行度与作业比例
     */
    public static final Integer DEFAULT_INPUT_PARALLELISM_RATIO = 1;

    /**
     * 默认输出并行度与作业比例
     */
    public static final Integer DEFAULT_OUTPUT_PARALLELISM_RATIO = 1;

    /**
     * 默认 ingest 作业是否开启去重
     */
    public static final Boolean DEFAULT_INGEST_DISTINCT_ENABLE = false;

    /**
     * 默认 ingest 作业去重状态过期时长
     */
    public static final long DEFAULT_INGEST_DISTINCT_STATE_MS_VALUE = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);

    /**
     * 默认 ingest 作业计算单元 slot 分组
     */
    public static final String DEFAULT_INGEST_IO_OPERATOR_SLOT_GROUP = "default";

    /**
     * 默认时区(亚洲/上海: UTC+8)
     */
    public static final TimeZone DEFAULT_TZ = TimeZone.getTimeZone("Asia/Shanghai");

    /**
     * 基础日,用于部分函数内计算时间差, 这里暂以20000101为准.
     */
    public static final LocalDate BASE_DATE = LocalDate.of(2000, 1, 1);

    /**
     * 应用ID KEY
     */
    public static final String APPID_KEY = "#app_id";

    /**
     * 对象类型 KEY
     */
    public static final String ENTITY_TYPE_KEY = "#entity_type";
    /**
     * Topic名称 KEY
     */
    public static final String TOPIC_KEY = "#topic";
    /**
     * Topic来源 KEY
     */
    public static final String TOPIC_SOURCE_KEY = "#topic_source";
    /**
     * Topic主题 KEY
     */
    public static final String TOPIC_SUBJECT_KEY = "#topic_subject";
    /**
     * 衍生事件 - 初始化标记 KEY
     */
    public static final String RECORD_ATTR_INIT_FLAG_KEY = "#init_flag";
    /**
     * 衍生事件 - 创建时间戳 KEY
     */
    public static final String RECORD_ATTR_CREATED_KEY = "#created";

    /**
     * 衍生事件KEY KEY
     */
    public static final String RECORD_DERIVE_EVENT_KEY_KEY = "#derive_event_key";

    public static final String INGEST_RECORD_TYPE_KEY = "type";

    public static final String INGEST_RECORD_APP_KEY = "app";

    public static final String INGEST_RECORD_DATA_KEY = "data";

    public static final String INGEST_KAFKA_HEADER_LOG_ID_KEY = "log_id";

    public static final String INGEST_KAFKA_HEADER_EVENT_KEY = "event";

    public static final String INGEST_KAFKA_HEADER_APP_KEY = "app";

    /**
     * Ingest 服务收集埋点事件的Topic
     * 事件数据示例
     * 埋点事件示例: { "type": "Event", "data": { "#channel": "TapTap".... }}
     */
    public static final String FUNNYDB_INGEST_RECEIVE_EVENTS = "funnydb-ingest-receive";

    /**
     * Ingest 管线错误数据输出Topic
     */
    public static final String FUNNYDB_INGEST_ERROR_EVENTS = "funnydb-ingest-pipeline-error";

    /**
     * Ingest 管线写出埋点事件的Topic
     * 事件数据示例
     * 埋点事件示例: { "type": "Event", "ip": "10.0.0.4", "app": "demo", "data": { "#channel": "TapTap".... }, "access_id": "FDI_laxeezCnehbjxhHYe4Qx", "ingest_time": 1702535259214 }
     */
    public static final String FUNNYDB_INGEST_TRACK_EVENTS = "funnydb-ingest-track-events";

    /**
     * Flink作业埋点处理写出的Topic
     * 事件数据示例
     * 埋点事件示例: { "type": "Event", "ip": "10.0.0.4", "app": "demo", "data": { "#channel": "TapTap".... }, "access_id": "FDI_laxeezCnehbjxhHYe4Qx", "ingest_time": 1702535259214 }
     */
    public static final String FUNNYDB_FLINK_TRACK_EVENTS = "funnydb-flink-track-events";

    /**
     * Ingest 管线写出用户/设备变更事件的Topic
     * 用户变更事件示例: { "type": "UserMutation", "ip": "10.110.2.50", "app": "demo", "data": { "#identify": "user-fake915171", "#log_id": "48be0db7-b59b-4fbc-a3fc-cec9f6574ddd", "#operate": "set", "#time": 1702535140202, "properties": { "#device_id": "device-fake1360798", "level": 4, "sex": "女", "user_last_login_time": 1702535140202 } }, "access_id": "demo", "ingest_time": 1702535140325 }
     * 设备变更事件示例: { "type": "DeviceMutation", "ip": "10.110.2.50", "app": "demo", "data": { "#identify": "device-fake1572681", "#log_id": "a539b58f-3330-4e42-9b3a-a080cedc07fa", "#operate": "add", "#time": 1702535140332, "device_total_online_time": 740 }, "access_id": "demo", "ingest_time": 1702535140396 }
     */
    public static final String FUNNYDB_INGEST_MUTATION_EVENTS = "funnydb-ingest-mutation-events";


    /**
     * 埋点事件固定字段值提取器映射
     */
    public static final Map<String, EventValueExtractor<TrackEvent, ?>> TRACK_EVENT_FIXED_FIELD_EXTRACTOR_MAPPING = Map.of(
            DATA_LIFECYCLE_KEY, TrackEvent::getDataLifecycle,
            LOG_ID_KEY, TrackEvent::getLogId,
            DEVICE_ID_KEY, TrackEvent::getDeviceId,
            USER_ID_KEY, TrackEvent::getUserId,
            EVENT_NAME_KEY, TrackEvent::getEvent,
            INGEST_TIME_KEY, TrackEvent::getIngestTime,
            LOG_TIME_KEY, TrackEvent::getTime,
            OP_KEY, TrackEvent::getOp
    );

    private Constant() {
    }
}
