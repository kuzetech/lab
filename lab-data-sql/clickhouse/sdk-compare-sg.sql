--海外正式服上报事件对比
WITH '2026-04-28' AS check_date
select event
from xiang_chang_hai_wai_zheng_shi_fu_488tfseh.events_receive_log
where event LIKE 'gameserver%'
  AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') = check_date
  AND event GLOBAL NOT in (select event
                           from xiang_chang_fu_wu_duan_go_sdk_qian_yi_xiao_bi3g7eve.events_receive_log
                           where event LIKE 'gameserver%'
                             AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') =
                                 check_date
                           GROUP BY event)
GROUP BY event
ORDER BY event desc;


--海外正式服上报事件数量对比
WITH '2026-04-28 08' AS check_time
SELECT event,
       sum(count_ingest)        AS total_ingest,
       sum(count_sdk)           AS total_sdk,
       total_ingest - total_sdk AS diff
FROM (
         SELECT event   AS event,
                count() AS count_ingest,
                0       AS count_sdk
         FROM xiang_chang_hai_wai_zheng_shi_fu_488tfseh.events_receive_log
         WHERE formatDateTime(fromUnixTimestamp64Milli(JSONExtractInt(message, '#time')), '%Y-%m-%d %H',
                              'Asia/Shanghai') = check_time
           AND event LIKE 'gameserver%'
         GROUP BY event
         UNION ALL
         SELECT event   as event,
                0       AS count_ingest,
                count() AS count_sdk
         FROM xiang_chang_fu_wu_duan_go_sdk_qian_yi_xiao_bi3g7eve.events_receive_log
         WHERE formatDateTime(fromUnixTimestamp64Milli(JSONExtractInt(message, '#time')), '%Y-%m-%d %H',
                              'Asia/Shanghai') = check_time
           AND event LIKE 'gameserver%'
         GROUP BY event
         )
GROUP BY event
HAVING diff != 0
ORDER BY abs(diff) DESC;


--海外先行服上报事件对比
WITH '2026-04-28' AS check_date
select event
from xiang_chang_hai_wai_xian_xing_fu_a7bhxmrb.events_receive_log
where event LIKE 'gameserver%'
  AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') = check_date
  AND event GLOBAL NOT in (select event
                           from xiang_chang_fu_wu_duan_go_sdk_qian_yi_xiao_fb9s2tsr.events_receive_log
                           where event LIKE 'gameserver%'
                             AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') =
                                 check_date
                           GROUP BY event)
GROUP BY event
ORDER BY event desc;


--海外先行服上报事件数量对比
WITH '2026-04-28 08' AS check_time
SELECT event,
       sum(count_ingest)        AS total_ingest,
       sum(count_sdk)           AS total_sdk,
       total_ingest - total_sdk AS diff
FROM (
         SELECT event   AS event,
                count() AS count_ingest,
                0       AS count_sdk
         FROM xiang_chang_hai_wai_xian_xing_fu_a7bhxmrb.events_receive_log
         WHERE formatDateTime(fromUnixTimestamp64Milli(JSONExtractInt(message, '#time')), '%Y-%m-%d %H',
                              'Asia/Shanghai') = check_time
           AND event LIKE 'gameserver%'
         GROUP BY event
         UNION ALL
         SELECT event   as event,
                0       AS count_ingest,
                count() AS count_sdk
         FROM xiang_chang_fu_wu_duan_go_sdk_qian_yi_xiao_fb9s2tsr.events_receive_log
         WHERE formatDateTime(fromUnixTimestamp64Milli(JSONExtractInt(message, '#time')), '%Y-%m-%d %H',
                              'Asia/Shanghai') = check_time
           AND event LIKE 'gameserver%'
         GROUP BY event
         )
GROUP BY event
HAVING diff != 0
ORDER BY abs(diff) DESC;