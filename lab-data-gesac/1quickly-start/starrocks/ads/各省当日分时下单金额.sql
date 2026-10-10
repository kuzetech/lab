select 
    dt,
    province_name,
    substring(time_point,12,2) time_hour,
    sum(order_total_amount_1d)
from mv_dws_trade_province_order_1d_30dl
group by dt,province_name,substring(time_point,12,2) 
having dt='2024-05-01';