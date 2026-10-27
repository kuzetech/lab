-- PostgreSQL uses schemas to represent the logical databases created by
-- mysql/initdb.  This keeps the table names and seed data aligned with MySQL.
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
END
$$;

CREATE SCHEMA IF NOT EXISTS app_db;
CREATE SCHEMA IF NOT EXISTS hive_metastore AUTHORIZATION hive;
CREATE SCHEMA IF NOT EXISTS dolphinscheduler AUTHORIZATION dolphinscheduler;
CREATE SCHEMA IF NOT EXISTS erp;
CREATE SCHEMA IF NOT EXISTS dga;

GRANT USAGE ON SCHEMA hive_metastore TO hive;
GRANT ALL PRIVILEGES ON SCHEMA dolphinscheduler TO dolphinscheduler;
