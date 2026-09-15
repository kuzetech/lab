SET 'execution.target' = 'yarn-per-job';
SET 'pipeline.name' = 'quickly-start-flink-sql-example';

CREATE TEMPORARY TABLE sample_numbers (
  id BIGINT,
  payload STRING
) WITH (
  'connector' = 'datagen',
  'rows-per-second' = '1',
  'fields.id.kind' = 'sequence',
  'fields.id.start' = '1',
  'fields.id.end' = '10'
);

CREATE TEMPORARY TABLE sample_print (
  id BIGINT,
  payload STRING
) WITH (
  'connector' = 'print'
);

INSERT INTO sample_print
SELECT id, payload
FROM sample_numbers;
