#!/bin/bash
set -ex

spark-submit \
--class org.apache.spark.examples.SparkPi \
--master local[2] \
--packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.2.2,org.apache.kafka:kafka-clients:2.4.1,org.apache.spark:spark-token-provider-kafka-0-10_2.12:3.2.2 \
/opt/bitnami/spark/examples/jars/spark-examples_2.12-3.2.2.jar \
10

cp /root/.ivy2/jars/* /opt/bitnami/spark/jars

spark-submit \
--master spark://master:7077 \
--executor-memory 1G \
--total-executor-cores 2 \
--conf spark.hadoop.fs.defaultFS=hdfs://namenode:8020 \
/spark_src/spark-block-aggregator-1.0-SNAPSHOT.jar /util/config-cluster.json