#!/bin/bash
set -e

clickhouse client -n <<-EOSQL
    use default;

    CREATE TABLE event_log_local on CLUSTER my
    (
        log_id          String                                                  COMMENT '日志ID',
        time            UInt64                                                  COMMENT '事件时间戳',
        dt              DateTime            MATERIALIZED toDateTime(time)       COMMENT '事件详细时间',
        event           LowCardinality(String)                                  COMMENT '事件名称',
        uid             UInt32                                                  COMMENT '用户ID'
    )
    ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{shard}/event_log_local', '{replica}')
    PARTITION BY toDate(time)
    ORDER BY (time, log_id);

    CREATE TABLE event_log_all ON CLUSTER my as event_log_local
    ENGINE = Distributed(my, default, event_log_local, rand());

EOSQL