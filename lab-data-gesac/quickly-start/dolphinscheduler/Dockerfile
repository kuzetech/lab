FROM apache/dolphinscheduler-standalone-server:3.4.2

USER root

ARG FLINK_VERSION=1.18.1
ARG HADOOP_VERSION=3.2.1

RUN apt-get update \
    && apt-get install -y procps \
    && rm -rf /var/lib/apt/lists/*

# Hadoop Client
COPY dep/hadoop-${HADOOP_VERSION}.tar.gz /tmp/hadoop.tar.gz
RUN tar -xzf /tmp/hadoop.tar.gz -C /opt \
    && rm /tmp/hadoop.tar.gz

# Flink Client
COPY dep/flink-${FLINK_VERSION}-bin-scala_2.12.tgz /tmp/flink.tgz
RUN tar -xzf /tmp/flink.tgz -C /opt \
    && mv /opt/flink-${FLINK_VERSION} /opt/flink \
    && rm /tmp/flink.tgz
COPY dep/flink-yarn-${FLINK_VERSION}.jar /opt/flink/lib/

ENV HADOOP_HOME=/opt/hadoop-3.2.1
ENV FLINK_HOME=/opt/flink

ENV HADOOP_CONF_DIR=/etc/hadoop
ENV YARN_CONF_DIR=/etc/hadoop

ENV PATH=${PATH}:${HADOOP_HOME}/bin:${FLINK_HOME}/bin
