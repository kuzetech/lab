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

mkdir -p /hadoop/dfs/name /hadoop/tmp

wait_for nn1 8020

if [ ! -d /hadoop/dfs/name/current ]; then
  bootstrapped=0
  for _ in $(seq 1 20); do
    if hdfs namenode -bootstrapStandby -nonInteractive; then
      bootstrapped=1
      break
    fi
    sleep 3
  done

  if [ "$bootstrapped" -ne 1 ]; then
    echo "Failed to bootstrap standby NameNode from nn1" >&2
    exit 1
  fi
fi

hdfs namenode &
nn_pid=$!

hdfs zkfc &
zkfc_pid=$!

while true; do
  if ! kill -0 "$nn_pid" >/dev/null 2>&1; then
    echo "Standby NameNode process exited" >&2
    wait "$nn_pid" || true
    exit 1
  fi

  if ! kill -0 "$zkfc_pid" >/dev/null 2>&1; then
    echo "Standby ZKFC process exited" >&2
    wait "$zkfc_pid" || true
    exit 1
  fi

  sleep 5
done
