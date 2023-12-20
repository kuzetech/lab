FROM openjdk:17-bullseye

ARG JAR_FILE

ADD target/${JAR_FILE} /app.jar

CMD ["java", "-jar","/app.jar"]