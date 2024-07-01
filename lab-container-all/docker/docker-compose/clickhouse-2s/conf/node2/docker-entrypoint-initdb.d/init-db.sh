!/bin/bash
set -e

clickhouse client -n <<-EOSQL
    use default;

    CREATE TABLE event_local
    (
        log_id      UInt32,
        dt          DateTime
    )
    ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
    PARTITION BY (toDate(dt))
    ORDER BY (log_id)
    settings assign_part_uuids = true;

    CREATE TABLE event_all as event_local ENGINE = Distributed(my, default, event_local, rand());

EOSQL