#!/bin/bash
set -e

clickhouse client -n <<-EOSQL
    use default;

    CREATE TABLE user_local
    (
        uid         UInt32                      COMMENT '用户ID',
        time        Date                        COMMENT '事件时间戳'
    )
    ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{shard}/user_local', '{replica}')
    PARTITION BY (time)
    ORDER BY (uid);

    CREATE TABLE user as user_local
    ENGINE = Distributed(my, default, user_local, rand());

    INSERT INTO default.user_local VALUES (1, '2021-10-01'),(2, '2021-10-02');

EOSQL