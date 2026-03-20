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

wait_for hive-metastore 9083
wait_for nn1 8020
wait_for nn2 8020

run_init_sql() {
  local dt
  dt="$(date +%F)"

  # HiveServer2 can listen on the port before it is fully ready for queries.
  wait_for localhost 10000 120

  for _ in $(seq 1 30); do
    if /opt/hive/bin/beeline \
      -u jdbc:hive2://localhost:10000 \
      -n hive \
      --hivevar dt="${dt}" \
      -f /opt/hive/conf/init-datagen.sql >/tmp/hive-init.log 2>&1; then
      echo "Hive init SQL executed for dt=${dt}"
      return 0
    fi
    sleep 2
  done

  echo "Hive init SQL failed after retries, last output:" >&2
  tail -n 80 /tmp/hive-init.log >&2 || true
  return 1
}

run_periodic_msck() {
  local interval="${HIVE_MSCK_INTERVAL_SEC:-300}"
  if [[ "${interval}" -le 0 ]]; then
    echo "Periodic MSCK is disabled (HIVE_MSCK_INTERVAL_SEC=${interval})"
    return 0
  fi

  # Ensure HiveServer2 is ready before running scheduled metadata repair.
  wait_for localhost 10000 120

  while true; do
    /opt/hive/bin/beeline \
      -u jdbc:hive2://localhost:10000 \
      -n hive \
      -e "use dwd; set hive.msck.path.validation=ignore; msck repair table flume_datagen_raw;" >>/tmp/hive-msck.log 2>&1 || true
    sleep "${interval}"
  done
}

# Run initialization in background to avoid blocking HiveServer2 startup.
( run_init_sql ) &
( run_periodic_msck ) &

exec /opt/hive/bin/hiveserver2 --hiveconf hive.root.logger=INFO,console
