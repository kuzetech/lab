
create table test_window (
    uid     String      COMMENT '用户ID',
    score   Int32       COMMENT '分数'
) engine = Memory;

insert into test_window values 
('A', 90),('A', 80),('A', 88),('A', 86),
('B', 91),('B', 95),('B', 90),('B', 66);

-- 需要先设置参数允许窗口函数
Set allow_experimental_window_functions = 1;

select 
    uid, 
    score, 
    sum(score) over(partition by uid order by score) sum,
    max(score) over(partition by uid order by score) max,
    min(score) over(partition by uid order by score) min,
    avg(score) over(partition by uid order by score) avg,
    count(score) over(partition by uid order by score) count
from test_window;

-- 用户 最大单月访问次数 和 累积访问次数

create table user_pv (
    uid String,
    time String,
    pv Int32
) engine = Memory;

insert into user_pv values
('A', '2015-01-01', 33),
('A', '2015-02-01', 10),
('A', '2015-03-01', 38),
('A', '2015-04-01', 20),
('B', '2015-01-01', 30),
('B', '2015-02-01', 15),
('B', '2015-03-01', 44),
('B', '2015-04-01', 35);


select 
    uid,
    topK(1)(max),
    topK(1)(sum)
from (
    select 
        uid, 
        time, 
        pv, 
        max(pv) over(partition by uid order by toDate(time) asc) max,
        sum(pv) over(partition by uid order by toDate(time) asc) sum
    from user_pv
    order by uid, sum desc
)
group by uid;

