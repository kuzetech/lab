#!/bin/bash
set -euo pipefail

flink run-application \
    -t yarn-application \
    --detached \
    -Dyarn.application.name=gesac_erp_order_cdc \
    -Dparallelism.default=2 \
    -Djobmanager.memory.process.size=768mb \
    -Dtaskmanager.memory.process.size=1024mb \
    -Dtaskmanager.numberOfTaskSlots=2 \
    /opt/flink/lib/paimon-flink-action-2.0.0.jar \
    mysql_sync_table \
    --warehouse hdfs://namenode:9000/paimon/hive \
    --database erp \
    --table ods_order_info_cdc \
    --partition_keys dt \
    --primary_keys dt,id \
    --computed_column  'dt=date_format(create_time,'yyyy-MM-dd')'  \
    --mysql_conf hostname=mysql \
    --mysql_conf username=root \
    --mysql_conf password=root_password \
    --mysql_conf database-name=erp \
    --mysql_conf table-name='order_info' \
    --mysql_conf server-time-zone=Asia/Shanghai \
    --catalog-conf metastore=hive \
    --catalog-conf uri=thrift://hive-metastore:9083 \
    --table-conf bucket=2 \
    --table-conf changelog-producer=input \
    --table-conf sink.parallelism=2 \
    --table_conf metastore.partitioned-table=true
