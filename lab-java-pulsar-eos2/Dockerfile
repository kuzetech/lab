# 阶段1: 构建阶段
FROM maven:3.9-eclipse-temurin-11 AS builder

WORKDIR /build

# 复制 pom.xml 并下载依赖（利用 Docker 层缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests -B

# 阶段2: 运行阶段
FROM eclipse-temurin:11-jre

WORKDIR /app

# 创建非 root 用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 创建数据目录
RUN mkdir -p /data/input /data/offset /app/logs && \
    chown -R appuser:appuser /data /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /build/target/lab-java-pulsar-eos2-1.0-SNAPSHOT.jar /app/app.jar

# 切换到非 root 用户
USER appuser

# 设置 JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 默认入口点
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar \"$@\"", "--"]

# 默认命令（可被 docker run 覆盖）
CMD ["--help"]
