
-- 查询数据分区
SELECT 
  table, 
  partition_id, 
  name, 
  rows, 
  intDiv(bytes_on_disk, 1048576) as bytes_on_disk_MB, 
  intDiv(data_compressed_bytes, 1048576) as data_compressed_bytes_MB, 
  intDiv(data_uncompressed_bytes, 1048576) as data_uncompressed_bytes_MB
FROM system.parts
WHERE database='dm' and table='customer_local' and active=1;

-- 删除分区
ALERT TABLE dm.customer_local ON CLUSTER cluster3s DROP PARTITION [partition_id or expr];

-- 复制分区
-- 表结构一致且分区键一致
ALTER TABLE B ON CLUSTER cluster3s REPLACE PARTITION [partition_id or expr] FROM A ;

-- 重置分区中的某一列的值
ALTER TABLE A ON CLUSTER cluster3s CLEAR COLUMN test IN PARTITION [partition_id or expr];

-- 卸载分区
ALTER TABLE A ON CLUSTER cluster3s DETACH PARTITION [partition_id or expr];

-- 装载分区
ALTER TABLE A ON CLUSTER cluster3s ATTACH PARTITION [partition_id or expr];

-- FETCH
ALTER TABLE test2 FETCH PARTITION 1 FROM '/clickhouse/tables/01/test';
