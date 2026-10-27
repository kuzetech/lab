select * from app_db.example_items;

SHOW config_file;

SELECT name, setting, unit, context, source
FROM pg_settings
where name like '%wal%';