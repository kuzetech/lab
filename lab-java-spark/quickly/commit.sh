#!/bin/bash
set -ex

spark-submit \
--master spark://master:7077 \
--executor-memory 1G \
--total-executor-cores 2 \
/spark_src/lab-java-spark-1.0-SNAPSHOT.jar