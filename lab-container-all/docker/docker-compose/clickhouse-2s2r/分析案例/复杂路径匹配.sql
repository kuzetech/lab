
create table test_path (
    uid             UInt32      COMMENT '用户ID',
    event_type      String      COMMENT '事件名称',
    ts_date         date        COMMENT '事件时间',
    ts_date_time    Datetime    COMMENT '事件详细时间'
) engine = Memory;


insert into test_path values
(1, 'login', '2020-01-01', '2020-01-01 01:00:00'),
(1, 'view', '2020-01-01', '2020-01-01 05:00:00'),
(1, 'buy', '2020-01-01', '2020-01-01 09:00:00'),
(1, 'buy', '2020-01-01', '2020-01-01 10:00:00'),
(1, 'view', '2020-01-01', '2020-01-01 12:00:00'),
(1, 'buy', '2020-01-01', '2020-01-01 14:00:00');


-- 给定期望的路径终点，途经点和最大时间间隔
-- 查询出符合条件的路径详情，及符合路径的用户数
-- 按照用户数降序排列

select 
    result_chain, 
    uniqCombined(uid) as user_count
from(
    with
        toUInt32(maxIf(ts_date_time, event_type = 'buy')) as buy_max_time,
        -- 将用户行为整理成 <时间，事件名>元组，并排序
        arrayCompact(arraySort(
            x -> x.1,
            arrayFilter(
                x -> x.1 <= buy_max_time,
                groupArray((toUInt32(ts_date_time), event_type))
            )
        )) as sorted_events,
        -- 获取原始行为链的下标数组
        arrayEnumerate(sorted_events) as event_idxs,
        -- 找出链条分界点，事件为 buy 或者 事件间隔超过 600 秒
        arrayFilter(
            (x, y, z) -> z.1 <= buy_max_time and (z.2 = 'buy' or y > 600),
            event_idxs, -- 作为下标x
            arrayDifference(sorted_events.1), -- 作为时间间隔y
            sorted_events -- 作为事件z
        ) as gap_idxs,
        -- 利用 arrayMap 和 has 函数获取下标数组的掩码（0和1组成），用于最终切分，1表示分界点
        -- 配合下面的 arraySplit 使用所以需要+1
        arrayMap( x -> x+1, gap_idxs) as gap_idxs_,
        arrayMap( x -> if(has(gap_idxs_, x), 1, 0), event_idxs) as gap_masks,
        -- 将行为链按分界点切分成单次访问的行为链，函数会将分界点作为新链的起点，所以前面要将分界点+1
        arraySplit((x, y) -> y, sorted_events, gap_masks) as split_events
    select
        uid, buy_max_time, sorted_events, event_idxs, gap_idxs_, gap_masks, split_events,
        arrayJoin(split_events) as event_chain_,
        arrayCompact(event_chain_.2) as event_chain,
        -- 也可以使用 hasAny 函数筛选途经点
        hasAll(event_chain, ['login', 'view']) as has_midway_hit,
        arrayStringConcat(arrayMap( x -> x, event_chain), ' -> ') as result_chain
    from test_path
    group by uid
    having length(event_chain)>1
) 
where event_chain[length(event_chain)]='buy' and has_midway_hit=1 
group by result_chain 
order by user_count 
desc limit 20