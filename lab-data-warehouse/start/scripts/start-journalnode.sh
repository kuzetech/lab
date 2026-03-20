#!/usr/bin/env bash
set -euo pipefail

mkdir -p /hadoop/dfs/journal /hadoop/tmp
exec hdfs journalnode
