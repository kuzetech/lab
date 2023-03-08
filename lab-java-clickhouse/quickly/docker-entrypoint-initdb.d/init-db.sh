#!/bin/bash
# 表不能使用 memory 类型，执行完该脚本后，服务器才启动，执行的插入语句数据都会丢失
set -e

clickhouse client -n <<-EOSQL

    create table default.action (
        uid     Int32,
        event   String,
        time    datetime
    )
    ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
    PARTITION BY uid
    ORDER BY uid;

EOSQL