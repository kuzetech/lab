#!/bin/bash
set -euo pipefail

yarn-session.sh -d \
  -Djobmanager.memory.process.size=1gb \
  -Dtaskmanager.memory.process.size=2gb \
  -Dtaskmanager.numberOfTaskSlots=2 \
  -Djobmanager.cpu.cores=1 \
  -Dtaskmanager.cpu.cores=2.0
