FROM registry.biubiubiu.io/funnydb/geoip:2023-07-31 AS GEOIP

FROM maven:3.8.6-eclipse-temurin-11 as build

ARG VERSION
ENV BASE_PATH=/opt/funnydb-event-process-flow-controller

COPY src $BASE_PATH/src
COPY pom.xml $BASE_PATH/pom.xml

COPY --from=GEOIP /geoip/*.mmdb $BASE_PATH/src/main/resources

# maven构建打包Flink应用
RUN mvn -f $BASE_PATH clean package -Dmaven.test.skip=true -Drevision="${VERSION}"

FROM registry.biubiubiu.io/funnydb/flink-apps-deploy:0.1.1-240306

ENV FROM_PATH=/opt/funnydb-event-process-flow-controller
ENV BASE_PATH=/opt/flink/artifacts/funnydb-event-process-flow-controller

RUN mkdir -p $BASE_PATH

# 将Flink应用移动至/opt/flink/artifacts目录下,以便flink-apps-job作业扫描上传至flink-appmanager
COPY --from=build $FROM_PATH/target/funnydb-event-process-flow-controller-*.jar $BASE_PATH/

ENTRYPOINT ["/opt/flink-apps-deploy/docker-entrypoint.sh"]