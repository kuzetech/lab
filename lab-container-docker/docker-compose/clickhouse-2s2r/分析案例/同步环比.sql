

create table month_earn (
    month_start     date      COMMENT '月份第一天',
    amount          Int32     COMMENT '销售金额'
) ENGINE = Memory;


insert into month_earn select * from (
    with toDate('2020-01-01') as start_date
    select 
        toStartOfMonth(start_date + (number*31)) month_start,
        (number+20)*100 amount
    from numbers(24)
);

-- 同比增长率 =（今年本期 - 去年同期）/ 去年同期
-- 环比增长率 =（今年本月 - 今年上月）/ 今年上月
-- 求 同比增长率 和 环比增长率

select 
    month_start,
    amount,
    neighbor(amount, -1)  as pre_month_amount,
    neighbor(amount, -12) as pre_year_amount,
    round((amount - pre_month_amount)/pre_month_amount, 4) as over_month,
    round((amount - pre_year_amount)/pre_year_amount, 4) as over_year
from (
    select * 
    from month_earn 
    order by month_start asc
)
order by month_start desc;

-- 当分母为0就会出现 inf
┌─month_start─┬─amount─┬─pre_month_amount─┬─pre_year_amount─┬─over_month─┬─over_year─┐
│  2020-02-01 │   2100 │             2000 │               0 │       0.05 │       inf │
└─────────────┴────────┴──────────────────┴─────────────────┴────────────┴───────────┘

--处理如下

select 
    month_start,
    amount,
    neighbor(amount, -1)  as pre_month_amount,
    neighbor(amount, -12) as pre_year_amount,
    if(pre_month_amount = 0, -999, round((amount - pre_month_amount)/pre_month_amount, 4)) as over_month,
    if(pre_year_amount = 0, -999, round((amount - pre_year_amount)/pre_year_amount, 4)) as over_year
from (
    select * 
    from month_earn 
    order by month_start asc
)
order by month_start desc;