FROM bde2020/hadoop-base:2.0.0-hadoop2.7.4-java8 AS hadoop

FROM bde2020/flume:latest

# Add Hadoop client jars so Flume HDFS sink can access HDFS APIs.
COPY --from=hadoop /opt/hadoop-2.7.4/share/hadoop/common/*.jar /usr/local/apache-flume/current/lib/
COPY --from=hadoop /opt/hadoop-2.7.4/share/hadoop/common/lib/*.jar /usr/local/apache-flume/current/lib/
COPY --from=hadoop /opt/hadoop-2.7.4/share/hadoop/hdfs/*.jar /usr/local/apache-flume/current/lib/
COPY --from=hadoop /opt/hadoop-2.7.4/share/hadoop/hdfs/lib/*.jar /usr/local/apache-flume/current/lib/
