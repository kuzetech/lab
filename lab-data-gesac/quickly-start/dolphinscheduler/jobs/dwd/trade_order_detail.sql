CREATE TABLE IF NOT EXISTS dwd_trade_order_detail
(   
    `detail_id`             BIGINT not null,
    `order_id`              BIGINT,
    `user_id`               BIGINT,
    `sku_id`                BIGINT,
    `province_id`           BIGINT,
    `create_time`           TIMESTAMP,
    `operate_time`          TIMESTAMP,
    `sku_num`               BIGINT,
    `order_price`   DECIMAL(16, 2),    
    `split_activity_amount` DECIMAL(16, 2),
    `split_coupon_amount`   DECIMAL(16, 2),
    `split_total_amount`    DECIMAL(16, 2),
    `dt`  varchar(20),
    CONSTRAINT `PK_id` PRIMARY KEY (`detail_id`,`dt`) NOT ENFORCED
)PARTITIONED BY (dt)
 WITH (
    'bucket' = '2',
    'path' = 'hdfs://hadoop102:8020/paimon/hive/gmall_lake2024.db/dwd_trade_order_detail',
    'sink.parallelism' = '2',
    'changelog-producer' = 'input',
    'metastore.partitioned-table'='true'
);

set 'table.exec.sink.upsert-materialize'='NONE';
insert into dwd_trade_order_detail
select 
     od.`id`,                 
     od.`order_id`,      
     oi.`user_id`,          
     od.`sku_id`,         
     oi.`province_id`,        
     oi.`create_time`,         
     oi.`operate_time`,      
     od.`sku_num`,         
     od.`order_price`,         
     od.`split_activity_amount`,
     od.`split_coupon_amount`,
     od.`split_total_amount`,   
     oi.`dt`
from ods_order_info_cdc oi
inner join ods_order_detail_cdc od on oi.id=od.order_id
and oi.`dt`=od.`dt`;