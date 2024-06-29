#!/bin/bash
set -ex

clickhouse-client -n <<-EOSQL
    CREATE TABLE IF NOT EXISTS system.query_log_all ON CLUSTER c1s1r as system.query_log
    ENGINE = Distributed(c1s1r, system, query_log);
EOSQL


# CREATE TABLE IF NOT EXISTS system.query_log_all ON CLUSTER demo as system.query_log ENGINE = Distributed(demo, system, query_log);

# CREATE TABLE IF NOT EXISTS system.query_thread_log_all ON CLUSTER demo as system.query_thread_log ENGINE = Distributed(demo, system, query_thread_log);

# CREATE TABLE IF NOT EXISTS system.parts_all ON CLUSTER demo as system.parts ENGINE = Distributed(demo, system, parts);
