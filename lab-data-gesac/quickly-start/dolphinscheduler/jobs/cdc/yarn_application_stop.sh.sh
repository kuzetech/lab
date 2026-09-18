#!/bin/bash
set -euo pipefail

export HADOOP_CLASSPATH="$(hadoop classpath)"

flink stop \
  -p  hdfs://namenode:9000/flink/savepoint/gmall_cdc \
  -yid application_1713772881362_0004 2e9796ad5ae5d2009d04072dc095bf0a