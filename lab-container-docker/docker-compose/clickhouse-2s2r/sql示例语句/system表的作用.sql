SELECT * FROM system.clusters;
SELECT * FROM system.macros;


select * from settings where name like '%quorum%';
select * from settings where name = 'select_sequential_consistency';



SELECT 
  name, 
  total_rows, 
  formatReadableSize(total_bytes) as total_size 
FROM system.tables 
WHERE database='sausage';

SELECT 
  name, 
  value 
FROM system.settings 
WHERE name='join_algorithm';

SELECT 
  name, 
  value 
FROM system.settings 
WHERE name like '%fetch%';

SELECT 
  table, 
  partition_id, 
  name, 
  rows, 
  formatReadableSize(bytes_on_disk) as bytes_on_disk, 
  formatReadableSize(data_compressed_bytes) as data_compressed_bytes, 
  formatReadableSize(data_uncompressed_bytes) as data_uncompressed_bytes
FROM system.parts
WHERE database='drm' and table='add_column_test';

select * from system.clusters where cluster='test';

SELECT table,column,
   sum(rows) AS rows,
   formatReadableSize(sum(column_data_compressed_bytes)) AS comp_bytes,
   formatReadableSize(sum(column_data_uncompressed_bytes)) AS uncomp_bytes
FROM system.parts_columns
WHERE database='dm' AND table = 'lineorder_local' AND active=1
GROUP BY table,column;


SELECT table,
   sum(rows) AS rows,
   formatReadableSize(sum(column_data_compressed_bytes)) AS comp_bytes,
   formatReadableSize(sum(column_data_uncompressed_bytes)) AS uncomp_bytes
FROM system.parts_columns
WHERE database='wide' AND table = 'login_steps_local' AND active=1
GROUP BY table;