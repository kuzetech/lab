
create database drm on cluster cluster3s;

CREATE TABLE drm.add_column_test as dm.lineorder_flat_left_local
ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{shard}/add_column_test', '{replica}', LO_ORDERDATE)
ORDER BY LO_ORDERKEY;

select count(*) from dm.lineorder_flat_left_local;

INSERT INTO drm.add_column_test SELECT * from dm.lineorder_flat_left_local;

select count(*) from drm.add_column_test;
