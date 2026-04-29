--国内正式服上报事件对比
WITH '2026-04-28' AS check_date
select event
from xiang_chang_pai_dui_zheng_shi_fu_afdo62u4.events_receive_log
where event LIKE 'gameserver%'
  AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') = check_date
  AND event GLOBAL NOT in (select event
                           from xiang_chang_fu_wu_duan_go_sdk_qian_yi_xiao_vjcsg4gx.events_receive_log
                           where event LIKE 'gameserver%'
                             AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') =
                                 check_date
                           GROUP BY event)
GROUP BY event
ORDER BY event desc;


--国内正式服上报事件数量对比
WITH '2026-04-29 08' AS check_time
SELECT event,
       sum(count_ingest)        AS total_ingest,
       sum(count_sdk)           AS total_sdk,
       total_ingest - total_sdk AS diff
FROM (
         SELECT event   AS event,
                count() AS count_ingest,
                0       AS count_sdk
         FROM xiang_chang_pai_dui_zheng_shi_fu_afdo62u4.events_receive_log
         WHERE formatDateTime(fromUnixTimestamp64Milli(JSONExtractInt(message, '#time')), '%Y-%m-%d %H',
                              'Asia/Shanghai') = check_time
           AND event LIKE 'gameserver%'
         GROUP BY event
         UNION ALL
         SELECT event   as event,
                0       AS count_ingest,
                count() AS count_sdk
         FROM xiang_chang_fu_wu_duan_go_sdk_qian_yi_xiao_vjcsg4gx.events_receive_log
         WHERE formatDateTime(fromUnixTimestamp64Milli(JSONExtractInt(message, '#time')), '%Y-%m-%d %H',
                              'Asia/Shanghai') = check_time
           AND event LIKE 'gameserver%'
         GROUP BY event
         )
GROUP BY event
HAVING diff != 0
ORDER BY abs(diff) DESC;


--国内先行服上报事件对比
WITH '2026-04-28' AS check_date
select event
from xiang_chang_pai_dui_xian_xing_fu_hlhyrn8i.events_receive_log
where event LIKE 'gameserver%'
  AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') = check_date
  AND event GLOBAL NOT in (select event
                           from xiang_chang_fu_wu_duan_go_sdk_qian_yi_xiao_pvdpb15p.events_receive_log
                           where event LIKE 'gameserver%'
                             AND formatDateTime(fromUnixTimestamp64Milli(`process_time`), '%Y-%m-%d', 'Asia/Shanghai') =
                                 check_date
                           GROUP BY event)
GROUP BY event
ORDER BY event desc;


--国内先行服上报事件数量对比
WITH '2026-04-29 08' AS check_time
SELECT event,
       sum(count_ingest)        AS total_ingest,
       sum(count_sdk)           AS total_sdk,
       total_ingest - total_sdk AS diff
FROM (
         SELECT event   AS event,
                count() AS count_ingest,
                0       AS count_sdk
         FROM xiang_chang_pai_dui_xian_xing_fu_hlhyrn8i.events_receive_log
         WHERE formatDateTime(fromUnixTimestamp64Milli(JSONExtractInt(message, '#time')), '%Y-%m-%d %H',
                              'Asia/Shanghai') = check_time
           AND event LIKE 'gameserver%'
         GROUP BY event
         UNION ALL
         SELECT event   as event,
                0       AS count_ingest,
                count() AS count_sdk
         FROM xiang_chang_fu_wu_duan_go_sdk_qian_yi_xiao_pvdpb15p.events_receive_log
         WHERE formatDateTime(fromUnixTimestamp64Milli(JSONExtractInt(message, '#time')), '%Y-%m-%d %H',
                              'Asia/Shanghai') = check_time
           AND event LIKE 'gameserver%'
         GROUP BY event
         )
GROUP BY event
HAVING diff != 0
ORDER BY abs(diff) DESC;