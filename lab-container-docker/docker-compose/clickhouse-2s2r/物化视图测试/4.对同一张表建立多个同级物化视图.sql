-- 创建 最大值 视图
CREATE TABLE mv_max_local ON CLUSTER my
(
    event           String,
    time            Date,
    agg_max         UInt64
)
ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
ORDER BY (time, event) 
PARTITION BY time;

CREATE MATERIALIZED VIEW mv_max ON CLUSTER my
TO mv_max_local
AS
SELECT event, time, max(v) AS agg_max
FROM source_local
GROUP BY event, time;


-- 创建 求和 视图
CREATE TABLE mv_sum_local ON CLUSTER my
(
    event           String,
    time            Date,
    agg_sum         UInt64
)
ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
ORDER BY (time, event) 
PARTITION BY time;

CREATE MATERIALIZED VIEW mv_sum ON CLUSTER my
TO mv_sum_local
AS
SELECT event, time, sum(v) AS agg_sum
FROM source_local
GROUP BY event, time;


-- 插入数据并查看结果
insert into source_local VALUES (1, 'view', '2022-01-01', 1),(2, 'view', '2022-01-01', 2);

select * from mv_max_local;
select * from mv_sum_local;


/**
一些总结
I don’t recommend to create 50 MVs (it’s a real case of one user who had problems 
with insertion performance) because if you have 50 MVs  during insert it will create at 
least 51 parts obviously it causes a lot of random I/O and works badly on HDD

MVs are processed in the alphabetical order （view 名字的字母顺序）

There is a setting parallel_view_processing. 
If this user setting is enabled (equal 1) then right after an insert into the source table is 
completed (part is written) MVs are processed in parallel by multiple threads. 
This setting can speed-up inserts if you have more than 2 MVs

**/

insert into mv_sum_local VALUES ('view', '2022-01-01', 3);