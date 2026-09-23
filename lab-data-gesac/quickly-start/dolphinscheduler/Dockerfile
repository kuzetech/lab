FROM apache/dolphinscheduler-standalone-server:3.4.2

USER root

ARG FLINK_VERSION=1.20.4
ARG HADOOP_VERSION=3.2.1
ARG MYSQL_CONNECTOR_VERSION=8.0.33
ARG PAIMON_FLINK_MINOR_VERSION=1.20
ARG PAIMON_FLINK_VERSION=2.0.0

# The DolphinScheduler base image does not include `rev`, which Flink's shell
# scripts use while parsing Java versions.
RUN printf '%s\n' '#!/usr/bin/awk -f' '{ for (i = length($0); i > 0; i--) printf "%s", substr($0, i, 1); printf "\n" }' > /usr/local/bin/rev \
    && chmod +x /usr/local/bin/rev

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
COPY dep/paimon-flink-${PAIMON_FLINK_MINOR_VERSION}-${PAIMON_FLINK_VERSION}.jar /opt/flink/lib/
COPY dep/flink-sql-connector-hive-3.1.3_2.12-${FLINK_VERSION}.jar /opt/flink/lib/
COPY dep/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.jar /opt/flink/lib/
COPY dep/flink-sql-connector-mysql-cdc-3.5.0.jar /opt/flink/lib/
COPY dep/flink-connector-kafka-3.4.0-1.20.jar /opt/flink/lib/
COPY dep/kafka-clients-3.9.2.jar /opt/flink/lib/
COPY dep/paimon-hive-connector-3.1-2.0.0.jar /opt/flink/lib/
COPY dep/paimon-flink-action-2.0.0.jar /opt/flink/lib/
RUN cp /opt/hadoop-${HADOOP_VERSION}/share/hadoop/mapreduce/hadoop-mapreduce-client-*.jar /opt/flink/lib/


# DolphinScheduler's MySQL profile needs the JDBC driver on both runtime and schema-upgrade classpaths.
COPY dep/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.jar /opt/dolphinscheduler/standalone-server/libs/
COPY dep/mysql-connector-j-${MYSQL_CONNECTOR_VERSION}.jar /opt/dolphinscheduler/tools/libs/

ENV HADOOP_HOME=/opt/hadoop-3.2.1
ENV FLINK_HOME=/opt/flink

ENV HADOOP_CONF_DIR=/etc/hadoop
ENV YARN_CONF_DIR=/etc/hadoop

ENV PATH=${PATH}:${HADOOP_HOME}/bin:${FLINK_HOME}/bin

# Make Hadoop/Flink environment available to DolphinScheduler tasks and
# ad-hoc non-interactive shells inside the container.
COPY env/default.sh /etc/profile.d/dolphinscheduler-default.sh
ENV BASH_ENV=/etc/profile.d/dolphinscheduler-default.sh
RUN printf '\n%s\n' 'source /etc/profile.d/dolphinscheduler-default.sh' >> /root/.bashrc
