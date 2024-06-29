CREATE TABLE next_local ON CLUSTER my
(
    time            Date,
    total             UInt64
)
ENGINE = ReplicatedSummingMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
ORDER BY (time);

CREATE MATERIALIZED VIEW mv_next ON CLUSTER my
TO mv2_local 
AS
SELECT time, sum(total) as total
FROM dest_local
GROUP BY time;

insert into source_local VALUES (10, 'view', '2022-01-08', 1),(11, 'view', '2022-01-08', 1);
