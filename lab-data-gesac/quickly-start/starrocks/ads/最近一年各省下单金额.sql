-- 冷热数据合并版本
select
    province_name,
    sum(order_total_amount_1d)
from(
    select 
        * 
    from mv_dws_trade_province_order_1d_30dl
    union all 
    select 
        * 
    from paimon_catalog.gmall_lake2024.dws_trade_province_order_1d 
    where str2date(dt,'%Y-%m-%d') > current_date() - interval 30 day 
    and str2date(dt,'%Y-%m-%d') < current_date() - interval 365 day
) order_all
group by province_name