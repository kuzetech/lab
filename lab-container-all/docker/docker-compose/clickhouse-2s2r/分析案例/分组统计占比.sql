docker exec -it clickhouse clickhouse-client --send_logs_level=trace

create table test_event_log (
    uid         UInt32      COMMENT '用户ID',
    event       String      COMMENT '事件名',
    ts_date     date        COMMENT '事件时间'
) engine = Memory;

insert into test_event_log values
(1, 'login', '2020-01-01'),
(1, 'view', '2020-01-01'),
(1, 'buy', '2020-01-01'),
(2, 'buy', '2020-01-01'),
(2, 'view', '2020-01-01'),
(2, 'buy', '2020-01-01');

select 
    ts_date, 
    uid,
    count(*) as day_total,
    (select count(*) from test_event_log) as total
from test_event_log
group by ts_date, uid