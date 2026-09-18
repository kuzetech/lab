-- 子公司名+业务域
create database gesac_trade;

use gesac_trade;

-- 结尾_30dl，标识该表保存了最近30天数据
CREATE MATERIALIZED VIEW mv_dws_trade_province_order_1d_30dl
PARTITION BY str2date (dt,'%Y-%m-%d')
PROPERTIES(
    "partition_ttl_number" = "30"
)
DISTRIBUTED BY HASH(`province_id`,`time_point`)
REFRESH ASYNC EVERY(INTERVAL 1 minute)
as
select
    `dt`,
    `time_point`,
    `time_interval`,
    `province_id`,
    `province_name`,
    `area_code`,
    `iso_code`,
    `iso_3166_2`,
    `order_original_amount_1d`,
    `activity_reduce_amount_1d`,
    `coupon_reduce_amount_1d`,
    `order_total_amount_1d`   
from paimon_catalog.gmall_lake2024.dws_trade_province_order_1d
where str2date (dt,'%Y-%m-%d') = current_date()