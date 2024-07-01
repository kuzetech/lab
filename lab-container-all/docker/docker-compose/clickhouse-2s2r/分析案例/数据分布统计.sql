select count(*) from wide.login_steps;
select count(*) from wide.login_steps_news;


select 
    toDate(dt) as dtTime,
    count(*)
from wide.login_steps
group by dtTime


select count(distinct pid)
from wide.login_steps
where toDate(dt) = '2021-09-26'

select pid 
from wide.login_steps 
where toDate(dt) = '2021-09-27'
order by time desc
limit 10