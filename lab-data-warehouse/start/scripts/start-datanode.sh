#!/usr/bin/env bash
set -euo pipefail

mkdir -p /hadoop/dfs/data /hadoop/tmp
exec hdfs datanode
