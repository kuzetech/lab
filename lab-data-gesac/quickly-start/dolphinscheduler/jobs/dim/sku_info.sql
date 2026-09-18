CREATE TABLE IF NOT EXISTS `dim_sku_info`  (
    `id` bigint,
    `spu_id` bigint,
    `price` decimal(10, 2),
    `sku_name` varchar(200),
    `sku_desc` varchar(2000),
    `weight` decimal(10, 2),
    `tm_id` bigint,
    `tm_name` varchar(100),
    `category3_id` bigint,
    `category3_name` varchar(50),
    `is_sale` tinyint,
    `create_time` TIMESTAMP,
    `operate_time` TIMESTAMP,
    CONSTRAINT `PK_id` PRIMARY KEY (`id`) NOT ENFORCED
) WITH (
    'path' = 'hdfs://hadoop102:8020/paimon/hive/gmall_lake2024.db/dim_sku_info',
    'bucket' = '2',
    'changelog-producer' = 'input',
    'sink.parallelism' = '2',
    'tag.automatic-creation' = 'process-time',
    'tag.creation-period' = 'daily',
    'tag.creation-delay' = '10 m',
    'tag.num-retained-max' = '30' ,
    'metastore.tag-to-partition'='dt',
    'metastore.tag-to-partition.preview'='process-time'
);

set 'table.exec.sink.upsert-materialize'='NONE';
insert into `dim_sku_info`
select 
    si.`id`,
    si.`spu_id`,
    si.`price`,
    si.`sku_name`,
    si.`sku_desc`,
    si.`weight`,
    si.`tm_id`,
    t.tm_name as `tm_name`,
    si.`category3_id`,
    c3.name as  `category3_name`,
    si.`is_sale`,
    si.`create_time`,
    si.`operate_time`   
from ods_sku_info_cdc si
left join ods_base_category3_cdc c3 on c3.id = si.category3_id
left join ods_base_trademark_cdc t  on t.id=si.tm_id;