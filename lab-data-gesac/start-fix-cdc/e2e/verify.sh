#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
COMPOSE=(docker compose -f "${ROOT_DIR}/compose.e2e.yaml")
SQL_JAR="${ROOT_DIR}/output/flink-sql-connector-postgres-cdc-3.7-SNAPSHOT.jar"

if [[ ! -s "${SQL_JAR}" ]]; then
    echo "Missing ${SQL_JAR}. Build the patched connector first:" >&2
    echo "  cd ${ROOT_DIR} && HOST_UID=$(id -u) HOST_GID=$(id -g) docker compose run --rm builder" >&2
    exit 1
fi

cleanup() {
    "${COMPOSE[@]}" down -v --remove-orphans >/dev/null 2>&1 || true
}

wait_for_log() {
    local expected=$1 attempts=180
    while (( attempts-- > 0 )); do
        if "${COMPOSE[@]}" logs taskmanager 2>&1 | grep -Fq "${expected}"; then
            return 0
        fi
        sleep 2
    done
    echo "Timed out waiting for CDC value: ${expected}" >&2
    "${COMPOSE[@]}" logs --no-color >&2
    return 1
}

wait_for_taskmanager() {
    local attempts=120
    while (( attempts-- > 0 )); do
        if "${COMPOSE[@]}" logs taskmanager 2>&1 | grep -Fq 'Successful registration at resource manager'; then
            return 0
        fi
        sleep 2
    done
    echo 'Timed out waiting for TaskManager registration.' >&2
    "${COMPOSE[@]}" logs --no-color >&2
    return 1
}

wait_for_jobmanager_rest() {
    local attempts=120
    while (( attempts-- > 0 )); do
        if "${COMPOSE[@]}" exec -T taskmanager bash -c '>/dev/tcp/jobmanager/8081' 2>/dev/null; then
            return 0
        fi
        sleep 2
    done
    echo 'Timed out waiting for JobManager REST.' >&2
    "${COMPOSE[@]}" logs --no-color >&2
    return 1
}

cleanup
trap cleanup EXIT
"${COMPOSE[@]}" up -d postgres jobmanager taskmanager
echo '==> Waiting for the TaskManager to register'
wait_for_taskmanager
echo '==> Waiting for the JobManager REST endpoint'
wait_for_jobmanager_rest
echo '==> Submitting the pgoutput CDC job'
"${COMPOSE[@]}" up -d sql-client
echo '==> Checking snapshot records with UTF-8 identifiers'
wait_for_log '快照-初始-A'
wait_for_log '快照-初始-B'
echo '==> Producing a post-snapshot streaming change'
"${COMPOSE[@]}" exec -T postgres psql -U flink -d cdc -c \
    "INSERT INTO \"验证模式\".\"订单🚀\" (\"识别码\", \"客户名称\", \"状态_🚀\") VALUES (3, '流式-验证-C', '已完成 🚀');"
echo '==> Checking the streaming record'
wait_for_log '流式-验证-C'
echo 'PASS: snapshot and pgoutput streaming both preserved UTF-8 table/column identifiers.'
