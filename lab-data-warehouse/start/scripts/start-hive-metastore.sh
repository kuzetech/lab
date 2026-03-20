#!/usr/bin/env bash
set -euo pipefail

wait_for() {
  local host="$1"
  local port="$2"
  local retries="${3:-90}"

  for _ in $(seq 1 "$retries"); do
    if (echo >"/dev/tcp/${host}/${port}") >/dev/null 2>&1; then
      return 0
    fi
    sleep 2
  done

  echo "Timed out waiting for ${host}:${port}" >&2
  return 1
}

wait_for nn1 8020
wait_for nn2 8020
wait_for hive-mysql 3306

hdfs dfs -mkdir -p /tmp /user/hive /user/hive/warehouse
hdfs dfs -chmod 1777 /user/hive/warehouse
# /tmp may be owned by another user in this test cluster bootstrap. Keep metastore startup resilient.
hdfs dfs -chmod 1777 /tmp || true

# Initialize metastore schema once; protect startup from hanging on schema checks.
if ! timeout 60 /opt/hive/bin/schematool -dbType mysql -info >/dev/null 2>&1; then
  timeout 180 /opt/hive/bin/schematool -dbType mysql -initSchema --verbose
fi

exec /opt/hive/bin/hive --service metastore
