select
    dt,
    log_id,
    count()
from event_all
group by dt, log_id
settings optimize_distributed_group_by_sharding_key = 1;