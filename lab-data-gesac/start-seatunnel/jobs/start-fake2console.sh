#!/bin/bash
set -euo pipefail

/opt/seatunnel/bin/seatunnel.sh \
-c /opt/seatunnel/config/v2.batch.config.template \
-m cluster