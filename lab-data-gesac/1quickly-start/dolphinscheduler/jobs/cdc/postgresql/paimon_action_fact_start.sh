#!/bin/bash
set -euo pipefail

flink run-application \
    -t yarn-application \
    --detached \
    -Dyarn.application.name=gesac_pg_erp_order_info_cdc \
    -Dparallelism.default=2 \
    -Djobmanager.memory.process.size=1gb \
    -Dtaskmanager.memory.process.size=2gb \
    -Dtaskmanager.numberOfTaskSlots=2 \
    /opt/flink/lib/paimon-flink-action-2.0.0.jar \
    postgres_sync_table \
    --warehouse hdfs://namenode:9000/paimon/hive \
    --database erp \
    --table ods_order_info_cdc \
    --computed_column  'dt=date_format(create_time,'yyyy-MM-dd')'  \
    --partition_keys dt \
    --primary_keys dt,id \
    --postgres_conf hostname=postgresql \
    --postgres_conf username=app_user \
    --postgres_conf password=app_password \
    --postgres_conf database-name=app_db \
    --postgres_conf schema-name=erp \
    --postgres_conf table-name='order_info' \
    --postgres_conf slot.name='paimon_cdc' \
    --postgres_conf decoding.plugin.name=pgoutput \
    --catalog-conf metastore=hive \
    --catalog-conf uri=thrift://hive-metastore:9083 \
    --table-conf bucket=2 \
    --table-conf changelog-producer=input \
    --table-conf sink.parallelism=2 \
    --table_conf metastore.partitioned-table=true