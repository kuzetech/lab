SELECT cluster,
       shard_num,
       replica_num,
       host_name,
       host_address,
       port,
       is_local
FROM system.clusters
WHERE cluster like 'all%'
ORDER BY cluster, shard_num, replica_num;


DROP TABLE default.mutation_local ON CLUSTER 'ga_ga_she_ji_x0c11yg5';

CREATE TABLE default.mutation_local ON CLUSTER 'ga_ga_she_ji_x0c11yg5'
(
    topic         LowCardinality(String),
    partition     Int32,
    offset        Int64,
    `#user_id`    String,
    `#event_time` UInt64,
    payload       String,
    `#dt`         Date MATERIALIZED toDate32(`#event_time` / 1000, 'Etc/GMT-8'),
    `#store_time` UInt64 MATERIALIZED toUnixTimestamp64Milli(now64())
)
    ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{uuid}/{shard}', '{replica}', `#store_time`)
        PARTITION BY `#dt`
        ORDER BY (topic, partition, offset)
        TTL toDateTime(`#event_time` / 1000, 'Etc/GMT-8') + toIntervalDay(7)
        SETTINGS index_granularity = 8192, assign_part_uuids = 1, part_moves_between_shards_enable = 1, part_moves_between_shards_delay_seconds = 2;


CREATE TABLE default.mutation ON CLUSTER 'ga_ga_she_ji_x0c11yg5'
    AS default.mutation_local
        ENGINE = Distributed(
                'ga_ga_she_ji_x0c11yg5',
                'default',
                mutation_local,
                rand()
                 );

select toStartOfFiveMinutes(fromUnixTimestamp64Milli(`#event_time`)) as start_time,
       count(if(topic = 'funnydb-mutation-test-output', 1, null))    as mutation,
       count(if(topic != 'funnydb-mutation-test-output', 1, null))   as flink
from mutation
where 1 = 1
group by start_time
order by start_time desc
limit 10;

