#!/bin/bash
set -ex

sleep 10s

clickhouse client  -n <<-EOSQL
    use default;

    CREATE TABLE event_local on cluster my(
        uid         String      COMMENT '用户',
        eventId     String      COMMENT '事件名称',
        eventTime   Date        COMMENT '事件时间'
    ) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}') PARTITION BY eventId ORDER BY (uid, eventTime);

    CREATE TABLE event_all on cluster my as event_local ENGINE = Distributed(my, default, event_local, rand());

EOSQL