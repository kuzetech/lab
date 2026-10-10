-- https://paimon.apache.org/docs/2.0/maintenance/configurations

select * from hive_catalog.erp.ods_order_info_cdc /*+ OPTIONS('scan.mode' = 'latest') */;