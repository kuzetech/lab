ALTER TABLE drm.add_column_test ADD COLUMN IF NOT EXISTS LO_NOTE String DEFAULT 'asdasdagwgaw@@#!@%@%@';
ALTER TABLE dm.lineorder_local drop COLUMN IF EXISTS LO_NOTE;

SELECT table,column,
   sum(rows) AS rows,
   formatReadableSize(sum(column_data_compressed_bytes)) AS comp_bytes,
   formatReadableSize(sum(column_data_uncompressed_bytes)) AS uncomp_bytes
FROM system.parts_columns
WHERE database='drm' and table='add_column_test' and active=1
GROUP BY table,column;

