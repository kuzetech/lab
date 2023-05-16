#!/bin/bash
set -ex

mkdir -p /checkpoint/event                                     
mkdir -p /checkpoint/user 

# --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.1.3 \

spark-submit \
--master spark://master:7077 \
--executor-memory 1G \
--total-executor-cores 2 \
/spark_src/spark-block-aggregator-1.0-SNAPSHOT.jar /spark_src/config-cluster.json