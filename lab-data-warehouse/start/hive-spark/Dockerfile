FROM bde2020/spark-master:2.3.0-hadoop2.7 AS sparkdist
FROM apache/hive:3.1.3

USER root

COPY --from=sparkdist /spark /opt/spark

# Prevent Spark's bundled Hive 1.2 jars from shadowing Hive 3.1 classes
# when HiveServer2 launches RemoteDriver via spark-submit.
RUN rm -f /opt/spark/jars/hive-*.spark2.jar

ENV SPARK_HOME=/opt/spark
ENV PATH=/opt/hive/bin:/opt/spark/bin:/opt/spark/sbin:${PATH}

USER hive
