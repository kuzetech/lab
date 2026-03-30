select currentDatabase();

formatDateTime(fromUnixTimestamp64Milli(`#created_time`), '%Y-%m-%d %H:%i:%s.%f', 'Asia/Shanghai')

SELECT
    multiIf(
    `#duration` < 0, '小于0',
    `#duration` = 0, '等于0',
    '大于0'
    ) AS duration_group,
    count() AS num,
    sum(count()) OVER () AS total,
    round(count() * 100 / total, 2) AS ratio
FROM events
WHERE `#dt` = '2026-01-14'
AND `#event` = '#user_online_duration'
AND `#sdk_type` = 'flink-derive'
GROUP BY duration_group;