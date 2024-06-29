#!/bin/bash
set -e

clickhouse client -n <<-EOSQL
    create database test;

    CREATE TABLE test.event_local
    (
        id              UInt32                      COMMENT '日志ID',
        event           LowCardinality(String)      COMMENT '事件名称'
    )
    ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{database}/{shard}/{table}', '{replica}')
    PARTITION BY (event)
    ORDER BY (id);

    CREATE TABLE test.event as test.event_local
    ENGINE = Distributed(my, test, event_local, rand());

    INSERT INTO test.event_local VALUES(1,'login');

EOSQL