CREATE TABLE source_local ON CLUSTER my
(
    id              UInt32,
    event           String,
    time            Date,
    v               UInt64
)
ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
ORDER BY (id)
PARTITION BY (time, event);

-- 分布式表创不创建都可以
CREATE TABLE source_all ON CLUSTER my as source_local
ENGINE = Distributed(my, default, source_local, rand());

-- 同时删除 zk 中的元数据
drop table source_local SYNC;


