CREATE CATALOG hive_catalog WITH (
    'type' = 'paimon',
    'metastore' = 'hive',
    'uri' = 'thrift://hive-metastore:9083',
    'hive-conf-dir' = '/opt/hive/conf',
    'warehouse' = 'hdfs://namenode:9000/paimon/hive'
);
USE CATALOG hive_catalog;
SET 'sql-client.execution.result-mode' = 'tableau';