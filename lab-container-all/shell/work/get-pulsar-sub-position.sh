#!/bin/bash

# Usage: ./get_sub_position.sh <topic> <subscription> <partition-count>

if [ $# -ne 3 ]; then
  echo "Usage: $0 <topic> <subscription> <partition-count>"
  echo "Example: $0 persistent://public/default/funnydb-ingest-receive funnydb-ingest-pipeline 9"
  exit 1
fi

TOPIC_BASE=$1
SUB_NAME=$2
PARTITIONS=$3

for (( i=0; i<$PARTITIONS; i++ ))
do
  PARTITION_TOPIC="${TOPIC_BASE}-partition-${i}"

  STATS_OUTPUT=$(pulsar-admin topics stats-internal "${PARTITION_TOPIC}" 2>/dev/null)

  if [ $? -ne 0 ]; then
    echo "  ❌ Failed to fetch stats for ${PARTITION_TOPIC}"
    continue
  fi

  # 提取订阅块
  SUB_BLOCK=$(echo "$STATS_OUTPUT" | sed -n "/\"$SUB_NAME\" *: {/,/}/p")

  if [ -z "$SUB_BLOCK" ]; then
    echo "  ⚠️  Subscription \"$SUB_NAME\" not found"
    continue
  fi

  # 提取 lastConsumedMessageId
  LAST_MSG_ID=$(echo "$SUB_BLOCK" | grep '"readPosition"' | sed -E 's/.*"readPosition"[[:space:]]*:[[:space:]]*"([^"]*)".*/\1/')

  if [ -z "$LAST_MSG_ID" ]; then
    echo "  ⚠️  readPosition not found (maybe no message consumed yet)"
  else
    echo "  ✅ $LAST_MSG_ID:${i}"
  fi
done
