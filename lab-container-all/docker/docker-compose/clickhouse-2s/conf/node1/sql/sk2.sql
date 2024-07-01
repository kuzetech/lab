select
    dt,
    log_id,
    count()
from event_all
group by sharding_key[dt, log_id];