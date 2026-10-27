#!/bin/bash
set -euo pipefail

flink stop \
  -p  hdfs://namenode:9000/flink/savepoints/gesac_pg_erp_order_info_cdc \
  -yid application_1793066963962_0005 943aaf25bc975e01f25261fb969b43fc