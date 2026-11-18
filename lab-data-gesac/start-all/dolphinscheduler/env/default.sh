#!/usr/bin/env bash
# Shared runtime settings for DolphinScheduler task processes.
export HADOOP_CONF_DIR="${HADOOP_CONF_DIR:-/etc/hadoop}"
export YARN_CONF_DIR="${YARN_CONF_DIR:-${HADOOP_CONF_DIR}}"
export FLINK_HOME="${FLINK_HOME:-/opt/flink}"

if [ -d "${FLINK_HOME}/bin" ]; then
  export PATH="${FLINK_HOME}/bin:${PATH}"
fi
