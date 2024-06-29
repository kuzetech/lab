CREATE DATABASE rdb ENGINE = Replicated('/clickhouse/databases/rdb', 's1', 'r2');

CREATE TABLE rdb.events (id UInt64, time DateTime64) ENGINE=ReplicatedMergeTree ORDER BY time;

CREATE TABLE rdb.event_local
(
    id              UInt32                      COMMENT '日志ID',
    event           LowCardinality(String)      COMMENT '事件名称'
)
ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
PARTITION BY (event)
ORDER BY (id);

INSERT INTO test.event_local VALUES(1,'login'),(2,'regist'),(3,'buy'),(4,'logout');