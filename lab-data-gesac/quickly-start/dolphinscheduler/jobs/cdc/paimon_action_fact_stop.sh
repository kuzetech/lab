#!/bin/bash
set -euo pipefail

flink stop \
  -p  hdfs://namenode:9000/flink/savepoints/gesac_erp_order_info_cdc \
  -yid application_1790131517465_0002 49b69c352a3220ad1678ac7769f2cfa4