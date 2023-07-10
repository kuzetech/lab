#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE TABLE "local_record" (
    "id" serial NOT NULL,
    PRIMARY KEY ("id"),
    "time" integer NOT NULL,
    "host" text NOT NULL,
    "client" text NOT NULL,
    "category" integer NOT NULL,
    "count" integer NOT NULL
  );

  CREATE TABLE "docker_record" (
    "id" serial NOT NULL,
    PRIMARY KEY ("id"),
    "time" integer NOT NULL,
    "host" text NOT NULL,
    "client" text NOT NULL,
    "category" integer NOT NULL,
    "count" integer NOT NULL
  );
EOSQL