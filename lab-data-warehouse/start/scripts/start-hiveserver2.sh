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

exec /opt/hive/bin/hiveserver2 --hiveconf hive.root.logger=INFO,console
