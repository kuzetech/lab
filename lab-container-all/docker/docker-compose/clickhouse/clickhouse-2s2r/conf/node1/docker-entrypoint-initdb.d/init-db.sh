#!/bin/bash
set -e

clickhouse client -n <<-EOSQL
    use default;

    CREATE TABLE customer_local (
        id UInt32,
        cname String
    ) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
    ORDER BY id;

    insert into customer_local values (1, 'ca');

    CREATE TABLE customer_all as customer_local ENGINE = Distributed(my, default, customer_local, rand());

    CREATE TABLE order_local (
        id UInt32,
        customer_id UInt32,
        part_id UInt32,
        supplier_id UInt32,
        oname String
    ) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
    ORDER BY id;

    insert into order_local values (1, 1, 1, 1, 'oa');

    CREATE TABLE order_all as order_local ENGINE = Distributed(my, default, order_local, rand());

    CREATE TABLE part_local (
        id UInt32,
        pname String
    ) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
    ORDER BY id;

    insert into part_local values (1, 'pa');

    CREATE TABLE part_all as part_local ENGINE = Distributed(my, default, part_local, rand());

    CREATE TABLE supplier_local (
        id UInt32,
        sname String
    ) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
    ORDER BY id;

    insert into supplier_local values (1, 'sa');

    CREATE TABLE supplier_all as supplier_local ENGINE = Distributed(my, default, supplier_local, rand());

EOSQL