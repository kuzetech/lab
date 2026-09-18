CREATE EXTERNAL CATALOG paimon_catalog
PROPERTIES
(
    "type" = "paimon",
    "paimon.catalog.type" ="hive",
    "paimon.catalog.warehouse"="hdfs://namenode:9000/paimon/hive",
    "hive.metastore.uris" = "thrift://hive-metastore:9083" 
);

show databases from paimon_catalog;

select 
    dt,
    province_name,
    substring(time_point,12,2) time_hour,
    sum(order_total_amount_1d)
from paimon_catalog.gmall_lake2024.dws_trade_province_order_1d 
group by dt,province_name,substring(time_point,12,2) 
having dt='2024-05-01';