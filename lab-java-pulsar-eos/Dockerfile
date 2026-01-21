# 多阶段构建 Dockerfile for Pulsar EOS File Processor

# 第一阶段：构建阶段
FROM maven:3.9-eclipse-temurin-11 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 和源代码
COPY pom.xml .
COPY src ./src

# 构建项目（跳过测试以加快构建速度）
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段
FROM eclipse-temurin:11-jre

# 设置工作目录
WORKDIR /app

# 创建日志目录
RUN mkdir -p /app/logs

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar /app/app.jar

# 复制配置文件（可选，也可以通过 volume 挂载）
COPY --from=builder /app/src/main/resources/config.yaml /app/config.yaml

# 设置环境变量
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# 暴露端口（如果需要）
# EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD ps aux | grep java || exit 1

# 设置入口点
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar $0 $@"]

# 默认命令（可以被覆盖）
CMD ["--config", "/app/config.yaml"]
