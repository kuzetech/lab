FROM maven:3.9.9-eclipse-temurin-11 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:11-jre
WORKDIR /app

COPY --from=build /workspace/target/funnydb-mutation-1.0-SNAPSHOT.jar /app/funnydb-mutation.jar
COPY docker/app /app/config

EXPOSE 8080

CMD ["java", "-jar", "/app/funnydb-mutation.jar", "/app/config/application-perf.properties"]
