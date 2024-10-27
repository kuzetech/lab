#!/bin/bash
set -e

mkdir -p /var/lib/clickhouse2
chown clickhouse:clickhouse /var/lib/clickhouse2

clickhouse client -n <<-EOSQL
    use default;

    CREATE TABLE IF NOT EXISTS test_local ON CLUSTER my
    (
        id  UInt8
    )
    ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{shard}/test_local', '{replica}')
    PARTITION BY (id)
    ORDER BY (id);

    CREATE TABLE IF NOT EXISTS test ON CLUSTER my as test_local
    ENGINE = Distributed(my, default, test_local, rand());

    INSERT INTO test VALUES(4),(5),(6);
EOSQL