FROM apache/streampark:v2.1.5

USER root
COPY flink-1.20.4-bin-scala_2.12.tgz /tmp/flink.tgz
RUN tar -xzf /tmp/flink.tgz -C /opt \
    && mv /opt/flink-1.20.4 /opt/flink \
    && rm /tmp/flink.tgz

ENV FLINK_HOME=/opt/flink
