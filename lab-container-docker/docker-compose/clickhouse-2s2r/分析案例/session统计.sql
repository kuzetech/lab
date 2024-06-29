
create table test_session (
    uid             UInt32      COMMENT '用户ID',
    eventType       String      COMMENT '事件名称',
    ts_date         date        COMMENT '事件时间',
    ts_date_time    Datetime    COMMENT '事件详细事件'
) engine = Memory;

insert into test_session values
(1, 'login', '2020-01-01', '2020-01-01 01:00:00'),
(1, 'login', '2020-01-01', '2020-01-01 02:00:00'),
(1, 'login', '2020-01-01', '2020-01-01 03:00:00'),
(2, 'login', '2020-01-01', '2020-01-01 01:00:00'),
(2, 'login', '2020-01-01', '2020-01-01 02:00:00'),
(2, 'login', '2020-01-01', '2020-01-01 03:00:00');

insert into test_session values
(3, 'login', '2020-01-01', '2020-01-01 01:01:00'),
(3, 'login', '2020-01-01', '2020-01-01 01:02:00'),
(3, 'login', '2020-01-01', '2020-01-01 01:03:00');

-- 按天统计所有用户的 session 总数，跨天的 session 会被切割

select
    ts_date,
    sum(length(session_gaps)) as session_count
from(
    with
        arraySort(groupArray(toUInt32(ts_date_time))) as times,
        arrayDifference(times) as times_diff
    select
        ts_date,
        uid,
        times,
        times_diff,
        arrayFilter(x -> x > 1800, times_diff) as session_gaps
    from test_session
    group by ts_date, uid
)
group by ts_date;
