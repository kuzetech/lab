#!/bin/bash
set -euo pipefail

export HADOOP_MAPRED_HOME="${HADOOP_MAPRED_HOME:-${HADOOP_HOME}}"
export HADOOP_CLASSPATH="$(hadoop classpath)"

flink run-application \
    -t yarn-application \
    --detached \
    -Dyarn.application.name=gmall_cdc \
    -Dparallelism.default=2 \
    -Djobmanager.memory.process.size=768mb \
    -Dtaskmanager.memory.process.size=1024mb \
    -Dtaskmanager.numberOfTaskSlots=2 \
    /opt/flink/lib/paimon-flink-action-2.0.0.jar \
    mysql-sync-database \
    --warehouse hdfs://namenode:9000/paimon/hive \
    --database gmall \
    --table-prefix "ods_" \
    --table-suffix "_cdc" \
    --mysql_conf hostname=mysql \
    --mysql_conf username=root \
    --mysql_conf password=root_password \
    --mysql_conf database-name=app_db \
    --mysql_conf server-time-zone=Asia/Shanghai \
    --catalog-conf metastore=hive \
    --catalog-conf uri=thrift://hive-metastore:9083 \
    --table-conf bucket=2 \
    --table-conf changelog-producer=input \
    --table-conf sink.parallelism=2 \
    --including-tables 'example_items'  
