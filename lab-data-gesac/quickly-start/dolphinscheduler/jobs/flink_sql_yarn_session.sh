#!/bin/bash
set -euo pipefail

export HADOOP_CLASSPATH="$(hadoop classpath)"

sql-client.sh \
  -i /opt/flink/conf/sql-init/sql-client-init.sql \
  -s yarn-session \
  -Dyarn.application.id=application_1789617543859_0006
