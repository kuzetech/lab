
create table test_sequence_match (
    uid          UInt32      COMMENT '用户ID',
    eventId      String      COMMENT '事件名称',
    eventTime    UInt64      COMMENT '事件时间',
) engine = Memory;


insert into test_sequence_match values
(1, 'login', 1), (1, 'view', 2), (1, 'view', 3), (1, 'buy', 4),
(2, 'login', 1), (2, 'view', 2), (2, 'buy', 3),
(3, 'login', 1), (3, 'view', 2), (3, 'login', 3), (3, 'view', 4), (3, 'buy', 5), (3, 'buy', 6);

-- 求 用户id = 3 的数据中
-- 经过路径 login - view - buy
-- 并且 login 跟 view 的时间间隔 >= 1秒
-- 并且 view 跟 buy 中间可以间隔任意事件

select 
    uid,
    sequenceMatch('(?1)(?t>=1)(?2).*(?3)')(
        eventTime,
        eventId = 'login',
        eventId = 'view',
        eventId = 'buy'
    ) as is_match 
from test_sequence_match
where uid = 3
group by uid;
