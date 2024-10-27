CREATE TABLE demo.events_kafka_v3
(
    `__raw` String
)
ENGINE = Kafka
SETTINGS kafka_broker_list = 'kafka-headless:9092', kafka_topic_list = '****', kafka_group_name = 'ch_demo_v3_events', kafka_format = 'JSONAsString';


CREATE MATERIALIZED VIEW demo.events_kafka_consumer_v3 TO demo.events_v3_local
(
    `#event` String,
    `#time` UInt64,
    `#log_id` String
) AS
WITH JSONExtract(substring(JSON_QUERY(__raw, '$.data'), 2, -1), 'Tuple(`#event` LowCardinality(String), `#time` UInt64, `#log_id` String)') AS __tuple
SELECT
    tupleElement(__tuple, '#event') AS `#event`,
    tupleElement(__tuple, '#time') AS `#time`,
    tupleElement(__tuple, '#log_id') AS `#log_id`
FROM demo.events_kafka_v3;

CREATE TABLE demo.events_v3_local
(
    `#event` LowCardinality(String) COMMENT '事件名称',
    `#time` UInt64 COMMENT '事件发生时间',
    `#dt` Date MATERIALIZED toDate32(`#time` / 1000, 'Etc/GMT-8') COMMENT '事件日期',
    `#store_time` UInt64 MATERIALIZED toUnixTimestamp64Milli(now64()) COMMENT '入库存储时间戳,毫秒级',
    `#log_id` String COMMENT '日志唯一ID'
)
ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{uuid}/{shard}', '{replica}', `#store_time`)
PARTITION BY `#dt`
ORDER BY (`#event`, `#log_id`)
SETTINGS index_granularity = 8192, storage_policy = 'jbod';

create table demo.events_v3 as demo.events_local ENGINE = Distributed('demo', 'demo', 'events_v3_local', rand());