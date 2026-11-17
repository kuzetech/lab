DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'hive') THEN
    CREATE ROLE hive LOGIN PASSWORD 'hive_password';
  ELSE
    ALTER ROLE hive LOGIN PASSWORD 'hive_password';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'dolphinscheduler') THEN
    CREATE ROLE dolphinscheduler LOGIN PASSWORD 'dolphinscheduler_password';
  ELSE
    ALTER ROLE dolphinscheduler LOGIN PASSWORD 'dolphinscheduler_password';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'dsj_cdc') THEN
    CREATE ROLE dsj_cdc LOGIN REPLICATION PASSWORD 'dsj_cdc_password';
  ELSE
    ALTER ROLE dsj_cdc LOGIN REPLICATION PASSWORD 'dsj_cdc_password';
  END IF;
END
$$;

CREATE SCHEMA IF NOT EXISTS app_db;
CREATE SCHEMA IF NOT EXISTS hive_metastore AUTHORIZATION hive;
CREATE SCHEMA IF NOT EXISTS dolphinscheduler AUTHORIZATION dolphinscheduler;
CREATE SCHEMA IF NOT EXISTS erp;
CREATE SCHEMA IF NOT EXISTS dga;

GRANT USAGE ON SCHEMA hive_metastore TO hive;
GRANT ALL PRIVILEGES ON SCHEMA dolphinscheduler TO dolphinscheduler;

# CDC相关
GRANT CONNECT ON DATABASE app_db TO dsj_cdc;
GRANT USAGE ON SCHEMA erp TO dsj_cdc;
GRANT SELECT ON ALL TABLES IN SCHEMA erp TO dsj_cdc;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA erp TO dsj_cdc;

ALTER DEFAULT PRIVILEGES IN SCHEMA erp GRANT SELECT ON TABLES TO dsj_cdc;
ALTER DEFAULT PRIVILEGES IN SCHEMA erp GRANT USAGE, SELECT ON SEQUENCES TO dsj_cdc;
