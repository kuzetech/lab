#!/bin/bash
set -euo pipefail

flink stop \
  -p  hdfs://namenode:9000/flink/savepoints/gesac_erp_dim_cdc \
  -yid application_1790064660363_0001 30d0d03b2ccbabe4f1dd8508ddde35bd