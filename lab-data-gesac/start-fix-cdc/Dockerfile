FROM maven:3.8.8-eclipse-temurin-11

ARG FLINK_CDC_COMMIT=1d89ff85ebc337650ef26c3e307957657bda7db4
ARG DEBEZIUM_COMMIT=c88a467e91a7acd0dd535c076a337439e23147ed

ENV MAVEN_OPTS="-Xmx3g"
WORKDIR /opt/src

RUN apt-get update \
    && apt-get install -y --no-install-recommends git \
    && rm -rf /var/lib/apt/lists/*

# Pin commits, rather than moving branch/tag names, so a rebuild is reproducible.
RUN git clone https://github.com/debezium/debezium.git debezium \
    && cd debezium \
    && git checkout --detach "${DEBEZIUM_COMMIT}" \
    && git clone https://github.com/apache/flink-cdc.git /opt/src/flink-cdc \
    && cd /opt/src/flink-cdc \
    && git checkout --detach "${FLINK_CDC_COMMIT}"

COPY patches/debezium-1.9.8-dbz-1682.patch /tmp/debezium-1.9.8-dbz-1682.patch
COPY patches/debezium-1.9.8-build-only-remove-avro-test-dependency.patch /tmp/debezium-1.9.8-build-only-remove-avro-test-dependency.patch
RUN cd /opt/src/debezium \
    && git apply --check /tmp/debezium-1.9.8-dbz-1682.patch \
    && git apply /tmp/debezium-1.9.8-dbz-1682.patch \
    && git apply --check /tmp/debezium-1.9.8-build-only-remove-avro-test-dependency.patch \
    && git apply /tmp/debezium-1.9.8-build-only-remove-avro-test-dependency.patch \
    && git diff --check

COPY maven-settings.xml /opt/maven-settings.xml
COPY build.sh /usr/local/bin/build-flink-cdc-postgres
RUN chmod +x /usr/local/bin/build-flink-cdc-postgres

ENTRYPOINT ["/usr/local/bin/build-flink-cdc-postgres"]
