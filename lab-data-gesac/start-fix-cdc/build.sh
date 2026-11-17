#!/usr/bin/env bash
set -Eeuo pipefail

DEBEZIUM_DIR=/opt/src/debezium
FLINK_CDC_DIR=/opt/src/flink-cdc
OUTPUT_DIR=/output
CONNECTOR_DIR=flink-cdc-connect/flink-cdc-source-connectors/flink-connector-postgres-cdc
SQL_CONNECTOR_DIR=flink-cdc-connect/flink-cdc-source-connectors/flink-sql-connector-postgres-cdc
PIPELINE_CONNECTOR_DIR=flink-cdc-connect/flink-cdc-pipeline-connectors/flink-cdc-pipeline-connector-postgres
ARTIFACT=flink-connector-postgres-cdc-3.7-SNAPSHOT.jar
SQL_ARTIFACT=flink-sql-connector-postgres-cdc-3.7-SNAPSHOT.jar
PIPELINE_ARTIFACT=flink-cdc-pipeline-connector-postgres-3.7-SNAPSHOT.jar

mkdir -p "${OUTPUT_DIR}"

echo '==> Installing patched Debezium 1.9.8.Final PostgreSQL connector'
cd "${DEBEZIUM_DIR}"
mvn -U -s /opt/maven-settings.xml -B -pl debezium-connector-postgres -am install -Dmaven.test.skip=true -DskipITs

echo '==> Compiling Flink CDC 3.7 PostgreSQL connector and its SQL distribution JAR'
cd "${FLINK_CDC_DIR}"
# Flink's reactor has compile-time dependencies on sibling test JARs.  Skip execution,
# but still produce those JARs.
mvn -U -s /opt/maven-settings.xml -B -pl "${SQL_CONNECTOR_DIR}" -am clean package -DskipTests

echo '==> Compiling Flink CDC 3.7 PostgreSQL pipeline connector'
mvn -U -s /opt/maven-settings.xml -B -pl "${PIPELINE_CONNECTOR_DIR}" -am clean package -DskipTests

SOURCE_JAR="${FLINK_CDC_DIR}/${CONNECTOR_DIR}/target/${ARTIFACT}"
SQL_SOURCE_JAR="${FLINK_CDC_DIR}/${SQL_CONNECTOR_DIR}/target/${SQL_ARTIFACT}"
PIPELINE_SOURCE_JAR="${FLINK_CDC_DIR}/${PIPELINE_CONNECTOR_DIR}/target/${PIPELINE_ARTIFACT}"
test -f "${SOURCE_JAR}"
test -f "${SQL_SOURCE_JAR}"
test -f "${PIPELINE_SOURCE_JAR}"

cp "${SOURCE_JAR}" "${OUTPUT_DIR}/${ARTIFACT}"
cp "${SQL_SOURCE_JAR}" "${OUTPUT_DIR}/${SQL_ARTIFACT}"
cp "${PIPELINE_SOURCE_JAR}" "${OUTPUT_DIR}/${PIPELINE_ARTIFACT}"
sha256sum "${OUTPUT_DIR}/${ARTIFACT}" | tee "${OUTPUT_DIR}/${ARTIFACT}.sha256"
sha256sum "${OUTPUT_DIR}/${SQL_ARTIFACT}" | tee "${OUTPUT_DIR}/${SQL_ARTIFACT}.sha256"
sha256sum "${OUTPUT_DIR}/${PIPELINE_ARTIFACT}" | tee "${OUTPUT_DIR}/${PIPELINE_ARTIFACT}.sha256"

cat > "${OUTPUT_DIR}/BUILD-INFO.txt" <<EOF
Flink CDC commit: $(git -C "${FLINK_CDC_DIR}" rev-parse HEAD)
Debezium base commit: $(git -C "${DEBEZIUM_DIR}" rev-parse HEAD)
Debezium patch: DBZ-1682 UTF-8 pgoutput identifier decoding
Artifact: ${ARTIFACT}
SQL artifact: ${SQL_ARTIFACT}
Pipeline artifact: ${PIPELINE_ARTIFACT}
EOF

chown -R "${HOST_UID}:${HOST_GID}" "${OUTPUT_DIR}" 2>/dev/null || true
echo "==> Done: ${OUTPUT_DIR}/${ARTIFACT}, ${OUTPUT_DIR}/${SQL_ARTIFACT}, ${OUTPUT_DIR}/${PIPELINE_ARTIFACT}"
