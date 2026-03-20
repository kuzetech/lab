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

wait_for jn1 8485
wait_for jn2 8485
wait_for jn3 8485
wait_for zk1 2181
wait_for zk2 2181
wait_for zk3 2181

if [ ! -d /hadoop/dfs/name/current ]; then
  hdfs namenode -format -force -nonInteractive
  hdfs zkfc -formatZK -nonInteractive || true
fi

hdfs namenode &
nn_pid=$!

hdfs zkfc &
zkfc_pid=$!

while true; do
  if ! kill -0 "$nn_pid" >/dev/null 2>&1; then
    echo "NameNode process exited" >&2
    wait "$nn_pid" || true
    exit 1
  fi

  if ! kill -0 "$zkfc_pid" >/dev/null 2>&1; then
    echo "ZKFC process exited" >&2
    wait "$zkfc_pid" || true
    exit 1
  fi

  sleep 5
done
