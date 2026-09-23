#!/bin/bash
set -euo pipefail

flink stop \
  -p  hdfs://namenode:9000/flink/savepoints/gesac_erp_dim_cdc \
  -yid application_1790125893234_0001 7c4be2580f06ec1f33905470c3e36f4e