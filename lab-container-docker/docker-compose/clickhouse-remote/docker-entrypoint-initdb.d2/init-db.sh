#!/bin/bash
set -e

clickhouse-client -h clickhouse1 --port 9000 -u default --query "SELECT concat('create database ', name, ';') FROM system.databases where name not in ('system', 'default')" --format TabSeparatedRaw > database.sql

clickhouse-client --multiquery < database.sql

clickhouse-client -h clickhouse1 --port 9000 -u default --query "SELECT concat(create_table_query, ';') FROM system.tables where database not in ('system')" --format TabSeparatedRaw > result.sql

clickhouse-client --multiquery < result.sql

clickhouse client -n <<-EOSQL
    
    INSERT INTO test.event_local SELECT * FROM remote('clickhouse1', test.event_local);

EOSQL