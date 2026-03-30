show databases ;

show tables;

show tables in xiang_chang_pai_dui_zheng_shi_fu_afdo62u4_global;
show create table xiang_chang_pai_dui_zheng_shi_fu_afdo62u4_global.devices_local;

show tables in xiang_chang_pai_dui_zheng_shi_fu_afdo62u4;
show create table xiang_chang_pai_dui_zheng_shi_fu_afdo62u4.devices_v;
show create table xiang_chang_pai_dui_zheng_shi_fu_afdo62u4.devices;

SELECT cluster FROM system.clusters LIMIT 10;

SELECT
    database,
    sum(rows) / 10000 AS total,
    total / sum(total) OVER () AS ratio
FROM system.parts
WHERE active = 1
AND (table = 'users_local' OR table = 'devices_local')
GROUP BY database
order by total desc
limit 10;

SELECT
    database,
    table,
    sum(rows) / 10000 AS total,
    sum(total) OVER (ORDER BY total DESC) AS cumulative_total
FROM system.parts
WHERE table = 'users_local' AND active = 1
GROUP BY database, table
order by total desc
limit 10;

SELECT
    database,
    table,
    sum(rows) / 10000 AS total,
    sum(total) OVER (ORDER BY total DESC) AS cumulative_total
FROM system.parts
WHERE table = 'users_local' AND active = 1
GROUP BY database, table
order by total desc
limit 10;