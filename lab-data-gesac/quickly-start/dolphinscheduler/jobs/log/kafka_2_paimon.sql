
CREATE temporary TABLE kafka_log (
    `event`    string,
    `page`      string,
    `ts`        bigint 
) WITH (
    'connector' = 'kafka',
    'topic' = 'source_log',
    'properties.bootstrap.servers' = 'kafka:29092',
    'scan.startup.mode' = 'latest-offset',
    'properties.group.id' = 'ods_log',
    'format' = 'json'
);


CREATE TABLE IF NOT EXISTS ods_log
(   
    `event`     string,
    `page`      string,
    `ts`        bigint,
    `dt`        varchar(10)
)PARTITIONED BY (dt) WITH (
    'bucket-key' = 'event',
    'bucket' = '2',
    'path' = 'hdfs://namenode:9000/paimon/hive/gesac_lake.db/ods_log',
    'sink.parallelism' = '2',
    'metastore.partitioned-table'='true'
);


insert into ods_log
select
    `event`,
    `page`,
    `ts`,
    DATE_FORMAT(TO_TIMESTAMP_LTZ(`ts`, 3), 'yyyy-MM-dd')
from kafka_log;

