#!/bin/bash

flink run \
  -t yarn-per-job \
  -Djobmanager.memory.process.size=1024m \
  -Dtaskmanager.memory.process.size=1024m \
  $FLINK_HOME/examples/streaming/WordCount.jar
