CREATE CATALOG hive_catalog WITH (
  'type' = 'hive',
  'default-database' = 'default',
  'hive-conf-dir' = '/opt/flink/conf',
  'hive-version' = '3.1.3'
);

USE CATALOG hive_catalog;

SHOW DATABASES;
