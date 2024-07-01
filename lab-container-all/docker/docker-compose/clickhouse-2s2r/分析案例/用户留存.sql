create table test_retention (
    uid         String      COMMENT '用户',
    eventId     String      COMMENT '事件名称',
    city        String      COMMENT '事件发生所在城市',
    region      String      COMMENT '大区名称',
    eventTime   DateTime    COMMENT '事件时间'
) engine = Memory;


insert into test_retention values
('小明', '签到', '厦门', '服务器1', '2020-01-01'), ('小明', '签到', '厦门', '服务器1', '2020-01-02'), ('小明', '签到', '厦门', '服务器1', '2020-01-03'),
('小东', '签到', '厦门', '服务器1', '2020-01-01'), ('小东', '签到', '厦门', '服务器1', '2020-01-02'),
('小张', '签到', '厦门', '服务器1', '2020-01-01'), ('小张', '签到', '厦门', '服务器1', '2020-01-03'),
('小林', '签到', '厦门', '服务器1', '2020-01-01'),
('小红', '签到', '北京', '服务器2', '2020-01-01'), ('小明', '签到', '北京', '服务器2', '2020-01-02'), ('小明', '北京', '厦门', '服务器2', '2020-01-03'),
('小绿', '签到', '北京', '服务器2', '2020-01-01'), ('小东', '签到', '北京', '服务器2', '2020-01-02'),
('小白', '签到', '北京', '服务器2', '2020-01-01'), ('小张', '签到', '北京', '服务器2', '2020-01-03'),
('小黑', '签到', '北京', '服务器2', '2020-01-01');


-- retention 函数可以方便的计算留存情况，该函数接受多个条件，以第一个条件的结算结果为基准
-- 观察后面的各个条件是否也满足，满足则为1，不满足则为0，最终返回0和1的数组。
-- 通过统计1的数量，即可计算出留存率

-- 下面的sql语句计算 次日重复下单率，三日重复下单率，七日重复下单率
with toDate('2020-01-01') as first_date
select

    sum(ret[1]) as original,
    arrayFilter(x -> x != '',groupArray(ret_uid[1])) as original_uid,

    sum(ret[2]) as one_day_ret,
    arrayFilter(x -> x != '',groupArray(ret_uid[2])) as one_day_uid,
    if(original = 0, 0, round(one_day_ret/original*100, 2)) as one_day_ratio,

    sum(ret[3]) as two_day_ret,
    arrayFilter(x -> x != '',groupArray(ret_uid[3])) as two_day_uid,
    if(original = 0, 0, round(two_day_ret/original*100, 2)) as two_day_ratio,

    sum(ret[4]) as three_day_ret,
    arrayFilter(x -> x != '',groupArray(ret_uid[4])) as three_day_uid,
    if(original = 0, 0, round(three_day_ret/original*100, 2)) as three_day_ratio,

    sum(ret[5]) as four_day_ret,
    arrayFilter(x -> x != '',groupArray(ret_uid[5])) as four_day_uid,
    if(original = 0, 0, round(four_day_ret/original*100, 2)) as four_day_ratio,

    sum(ret[6]) as five_day_ret,
    arrayFilter(x -> x != '',groupArray(ret_uid[6])) as five_day_uid,
    if(original = 0, 0, round(five_day_ret/original*100, 2)) as five_day_ratio,

    sum(ret[7]) as six_day_ret,
    arrayFilter(x -> x != '',groupArray(ret_uid[7])) as six_day_uid,
    if(original = 0, 0, round(six_day_ret/original*100, 2)) as six_day_ratio,

    sum(ret[8]) as seven_day_ret,
    arrayFilter(x -> x != '',groupArray(ret_uid[8])) as seven_day_uid,
    if(original = 0, 0, round(seven_day_ret/original*100, 2)) as seven_day_ratio

from (
    select
        uid,
        retention(
            eventTime = first_date,
            eventTime = first_date + interval 1 day,
            eventTime = first_date + interval 2 day,
            eventTime = first_date + interval 3 day,
            eventTime = first_date + interval 4 day,
            eventTime = first_date + interval 5 day,
            eventTime = first_date + interval 6 day,
            eventTime = first_date + interval 7 day
        ) as ret,
        arrayMap(x -> if(x == 1, uid, ''), ret) as ret_uid
    from (
        select *
        from (
            select *
            from test_retention
            where (eventId = '签到' and eventTime = first_date and city != '上海')  -- 初始事件单独筛选条件
            or (eventId = '签到' and eventTime between first_date + interval 1 day and first_date + interval 7 day and city != '上海') -- 回访事件单独筛选条件
        )
        where city != '海南'  --  全局筛选条件
        and eventTime between first_date and first_date + interval 7 day -- 该行是 事件时间筛选区间
    )
    group by uid
);
