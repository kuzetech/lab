
-- 查询数据分区
SELECT 
  table, 
  name, 
  rows
FROM system.parts
WHERE database='default' and table='user_local' and active=1;

-- FETCH
ALTER TABLE user_local FETCH PART '20211001_0_0_0' FROM '/clickhouse/tables/03/user_local';
ALTER TABLE user_local FETCH PART '20211002_0_0_0' FROM '/clickhouse/tables/03/user_local';

-- 装载分区
ALTER TABLE user_local ATTACH PART '20211001_0_0_0';
ALTER TABLE user_local ATTACH PART '20211002_0_0_0';

