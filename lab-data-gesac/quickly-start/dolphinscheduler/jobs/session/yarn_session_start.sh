#!/bin/bash
set -euo pipefail

yarn-session.sh -d \
  -Djobmanager.memory.process.size=512m \
  -Dtaskmanager.memory.process.size=768m \
  -Dtaskmanager.numberOfTaskSlots=2 \
  -Djobmanager.cpu.cores=0.5 \
  -Dtaskmanager.cpu.cores=1.0
