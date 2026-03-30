--国内先行服上报事件对比
select event
from xiang_chang_pai_dui_xian_xing_fu_hlhyrn8i.events_receive_log
where event LIKE 'gameserver%'
AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') = '2026-03-18'
AND event GLOBAL NOT in (
  select event
  from xiang_chang_fu_wu_duan_go_sdk_qian_yi_xiao_pvdpb15p.events_receive_log
  where event LIKE 'gameserver%'
  AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') = '2026-03-18'
  GROUP BY event
)
GROUP BY event
ORDER BY event desc;

--海外正式服上报事件对比
select event
from xiang_chang_hai_wai_zheng_shi_fu_488tfseh.events_receive_log
where event LIKE 'gameserver%'
AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') = '2026-03-18'
AND event GLOBAL NOT in (
  select event
  from xiang_chang_fu_wu_duan_go_sdk_qian_yi_xiao_bi3g7eve.events_receive_log
  where event LIKE 'gameserver%'
  AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') = '2026-03-18'
  GROUP BY event
)
GROUP BY event
ORDER BY event desc;

--海外正式服上报事件数量对比
SELECT
    event,
    sum(count_ingest) AS total_ingest,
    sum(count_sdk) AS total_sdk,
    total_ingest - total_sdk AS diff
FROM (
    SELECT
        `#event` as event,
        count() AS count_ingest,
        0 AS count_sdk
    FROM xiang_chang_hai_wai_zheng_shi_fu_488tfseh.events
    WHERE `#dt` = '2026-03-19'
    AND formatDateTime(fromUnixTimestamp64Milli(`#time`), '%H', 'Asia/Shanghai') = '08'
    AND `#event` LIKE 'gameserver%'
    GROUP BY `#event`
    UNION ALL
    SELECT
        `#event` as event,
        0 AS count_ingest,
        count() AS count_sdk
    FROM xiang_chang_fu_wu_duan_go_sdk_qian_yi_xiao_bi3g7eve.events
    WHERE `#dt` = '2026-03-19'
    AND formatDateTime(fromUnixTimestamp64Milli(`#time`), '%H', 'Asia/Shanghai') = '08'
    AND `#event` LIKE 'gameserver%'
    GROUP BY `#event`
)
GROUP BY event
HAVING diff != 0
ORDER BY abs(diff) DESC;