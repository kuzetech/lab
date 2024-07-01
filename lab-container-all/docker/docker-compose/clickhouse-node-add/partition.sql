
-- 查询数据分区
SELECT 
  table, 
  name, 
  rows
FROM system.parts
WHERE database='default' and table='user_local' and active=1;

-- FETCH
ALTER TABLE user_local FETCH PART '20211002_0_0_0' FROM '/clickhouse/tables/01/user_local';
ALTER TABLE user_local FETCH PART '20211002_0_0_0' FROM '/clickhouse/tables/02/user_local';

-- 装载分区
ALTER TABLE user_local ATTACH PART '20211002_0_0_0';

-- 卸载分区
ALTER TABLE user_local DETACH PART '20211002_0_0_0';

-- 删除分区
ALTER TABLE user_local DROP DETACHED PART '20211002_0_0_0';

