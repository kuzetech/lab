CREATE TABLE dest_local ON CLUSTER my
(
    event           String,
    time            Date,
    total           UInt64
)
ENGINE = ReplicatedSummingMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
ORDER BY (time, event)
PARTITION BY time;

CREATE MATERIALIZED VIEW mv_desc ON CLUSTER my
TO dest_local 
AS
SELECT event, time, sum(v) AS total
FROM source_local
GROUP BY event, time;


-- 写入测试数据
insert into source_local VALUES (1, 'view', '2022-01-01', 1),(2, 'view', '2022-01-02', 1);
insert into source_local VALUES (3, 'view', '2022-01-03', 1),(4, 'view', '2022-01-03', 1);

insert into source_local VALUES (5, 'view', '2022-01-04', 1);
insert into source_local VALUES (6, 'view', '2022-01-04', 1);

optimize table dest_local;


/**
clickhouse 有 insert_deduplication 的特性，
如果 block 插入 source 表成功后，在插入 mv 的过程中失败，客户端将会重新上传 block
这时候 source 表会校验 block，一致的情况下不写入，因此 mv 就丢失了这部分数据

因此需要： SET deduplicate_blocks_in_dependent_materialized_views=1;
可以通过语句查询该参数： select * from system.settings where name = 'deduplicate_blocks_in_dependent_materialized_views';

设置了该参数，视图会自己判断 block 是否重复，判断的标准不是原来的 block 数据，而是 block 按照视图聚合后的所有统计数据是否相同
这就有可能导致错误，不同的两批数据，聚合出来的结果一致也会被当成重复数据。

为了解决这个问题，需要在 source 表上增加 uniq_id, 物化视图使用 any(id) 的形式，这样可以保证每一批数据的统计结果都不一样
如果嫌弃 id 字段占用空间，可以采用 TTL 或者 ‘alter table dest_uniq_id_local clear column id’;
具体实验可以参照下面：

**/

CREATE TABLE dest_uniq_id_local ON CLUSTER my
(
    id              UInt32,
    event           String,
    time            Date,
    total           UInt64
)
ENGINE = ReplicatedSummingMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
ORDER BY (time, event)
PARTITION BY time;

CREATE MATERIALIZED VIEW mv_dest_uniq_id ON CLUSTER my 
TO dest_uniq_id_local 
AS 
SELECT event, time, any(id) as id, sum(v) AS total 
FROM source_local 
GROUP BY event, time;

insert into source_local VALUES (11, 'view', '2022-01-06', 2),(12, 'view', '2022-01-06', 2);
insert into source_local VALUES (13, 'view', '2022-01-06', 2),(14, 'view', '2022-01-06', 2);