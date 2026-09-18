-- 交易域省份粒度订单最近1日汇总表
CREATE TABLE IF NOT EXISTS  dws_trade_province_order_1d
(     
    `dt` varchar(20),
    `time_point`                varchar(20) not null,
    `time_interval`           varchar(20) not null,
    `province_id`               bigint,
    `province_name`             varchar(20),
    `area_code`                 varchar(20),
    `iso_code`                  varchar(20),
    `iso_3166_2`                varchar(20),
    `order_original_amount_1d`  decimal(16, 2)  ,
    `activity_reduce_amount_1d` decimal(16, 2)  ,
    `coupon_reduce_amount_1d`   decimal(16, 2)  ,
    `order_total_amount_1d`     decimal(16, 2) , 
    CONSTRAINT `PK_id` PRIMARY KEY (`dt`,`time_point`,`time_interval`,`province_id`,`province_name`,`area_code`
    ,`iso_code`,`iso_3166_2` ) NOT ENFORCED
)
PARTITIONED BY (`dt`)
with(
    'merge-engine' = 'aggregation',
    'fields.order_original_amount_1d.aggregate-function' = 'sum',
    'fields.activity_reduce_amount_1d.aggregate-function' = 'sum',
    'fields.coupon_reduce_amount_1d.aggregate-function' = 'sum',
    'fields.order_total_amount_1d.aggregate-function' = 'sum',
    'bucket' = '2',
    'path' = 'hdfs://hadoop102:8020/paimon/hive/gmall_lake2024.db/dws_trade_province_order_1d',
    'sink.parallelism' = '2',
    'changelog-producer' = 'lookup',
    'metastore.partitioned-table'='true'
);

set 'table.exec.sink.upsert-materialize'='NONE';
insert into dws_trade_province_order_1d
select 
    od.dt ,
    date_format(od.`create_time`,'yyyy-MM-dd HH:mm') as time_point,
    '1m' as time_interval,
    od.`province_id` ,
    pv.`province_name`,
    pv.`area_code` ,
    pv.`iso_code` ,
    pv.`iso_3166_2` ,
    od.sku_num * od.order_price   ,
    od.`split_activity_amount` ,
    od.`split_coupon_amount`  ,
    od.`split_total_amount`    
from (
    select 
        o.*,
        PROCTIME() as proc_time 
    from dwd_trade_order_detail  o
) as od
join dim_province FOR SYSTEM_TIME AS OF od.proc_time AS pv on od.province_id=pv.id;