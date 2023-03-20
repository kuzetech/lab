#!/bin/bash
set -ex

# 有时候我们会将公用包去掉，尽量减少包的大小
# spark-submit 默认会添加 /root/.ivy2/jar 中的包到路径中
# 如果我们仅仅在 spark-submit 中增加 --packages 参数，下载的依赖包存储在 /root/.ivy2/cache 中，就会导致程序找不到依赖
# 只有在程序成功启动后才会将 jar 包从 cache 文件夹中移动到 jar 文件夹中
# 因此我们先启动一个 SparkPi 程序将包下载下来并移动到 jar 文件夹中

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
/spark_src/lab-java-spark-1.0-SNAPSHOT.jar