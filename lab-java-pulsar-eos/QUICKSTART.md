# Pulsar EOS 文件处理器 - 快速开始指南

## 环境准备

### 1. 启动服务

```bash
# 启动 Pulsar 和 MySQL
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看 Pulsar 日志
docker-compose logs -f pulsar

# 查看 MySQL 日志
docker-compose logs -f mysql
```

### 2. 验证服务

```bash
# 验证 Pulsar
curl http://localhost:8080/admin/v2/brokers/health

# 验证 MySQL
docker exec -it mysql-offset-store mysql -uroot -proot123456 -e "SHOW DATABASES;"
```

## 生成测试数据

```bash
# 给脚本添加执行权限
chmod +x scripts/test-data-gen.sh

# 生成 1000 行测试数据到 /tmp/test.log
./scripts/test-data-gen.sh 1000 /tmp/test.log

# 生成 5000 行测试数据到自定义路径
./scripts/test-data-gen.sh 5000 /tmp/my-test.log
```

## 编译项目

```bash
# 清理并编译
mvn clean compile

# 打包（生成可执行 JAR）
mvn clean package

# 打包后的文件在: target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar
```

## 运行程序

### 方式 1: 使用默认配置

```bash
# 使用 classpath 中的 config.yaml（需要先修改配置文件中的 file.path）
mvn exec:java -Dexec.mainClass="com.kuzetech.bigdata.pulsar.eos.Main"
```

### 方式 2: 使用命令行参数覆盖

```bash
# 使用命令行参数指定文件路径
mvn exec:java -Dexec.mainClass="com.kuzetech.bigdata.pulsar.eos.Main" \
  -Dexec.args="--file /tmp/test.log"

# 指定多个参数
mvn exec:java -Dexec.mainClass="com.kuzetech.bigdata.pulsar.eos.Main" \
  -Dexec.args="--file /tmp/test.log --topic my-custom-topic --batch-size 50"
```

### 方式 3: 使用打包后的 JAR

```bash
# 首先打包
mvn clean package

# 运行 JAR
java -jar target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar --file /tmp/test.log

# 使用自定义配置文件
java -jar target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar \
  --config /path/to/custom-config.yaml \
  --file /tmp/test.log
```

## 验证结果

### 1. 查看 MySQL 中的偏移量记录

```bash
# 连接到 MySQL
docker exec -it mysql-offset-store mysql -upulsar_user -ppulsar_pass pulsar_offset

# 查看文件处理记录
SELECT * FROM file_offsets;

# 查看事务日志
SELECT * FROM transaction_log ORDER BY created_at DESC LIMIT 10;
```

### 2. 消费 Pulsar 消息

```bash
# 进入 Pulsar 容器
docker exec -it pulsar-standalone /bin/bash

# 消费消息
bin/pulsar-client consume \
  persistent://public/default/log-messages \
  --subscription-name test-sub \
  --num-messages 10

# 查看主题统计
bin/pulsar-admin topics stats persistent://public/default/log-messages
```

## 断点续传测试

```bash
# 1. 生成测试文件
./scripts/test-data-gen.sh 1000 /tmp/test.log

# 2. 第一次运行（处理部分数据后手动终止）
java -jar target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar --file /tmp/test.log
# Ctrl+C 终止

# 3. 查看已处理的偏移量
docker exec -it mysql-offset-store mysql -upulsar_user -ppulsar_pass pulsar_offset \
  -e "SELECT file_path, processed_lines, status FROM file_offsets;"

# 4. 再次运行（将从上次中断处继续）
java -jar target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar --file /tmp/test.log
```

## 配置说明

主要配置项（config.yaml）：

```yaml
pulsar:
  serviceUrl: pulsar://localhost:6650    # Pulsar 服务地址
  topic: persistent://public/default/log-messages  # 目标主题

mysql:
  host: localhost                         # MySQL 地址
  port: 3306
  database: pulsar_offset
  username: pulsar_user
  password: pulsar_pass

file:
  path: /tmp/test.log                    # 文件路径（可被命令行参数覆盖）
  batchSize: 100                         # 每批处理的行数
  encoding: UTF-8                        # 文件编码

retry:
  maxAttempts: 3                         # 最大重试次数
  delayMs: 1000                          # 初始重试延迟
  multiplier: 2.0                        # 延迟倍数
  maxDelayMs: 10000                      # 最大延迟
```

## 命令行参数

```bash
--config <path>       # 配置文件路径（默认: classpath:config.yaml）
--file <path>         # 日志文件路径（覆盖配置）
--topic <topic>       # Pulsar 主题（覆盖配置）
--pulsar-url <url>    # Pulsar 服务 URL（覆盖配置）
--batch-size <size>   # 批处理大小（覆盖配置）
```

## 常见问题

### 1. 连接 Pulsar 失败

```bash
# 确认 Pulsar 服务正常
docker-compose ps
curl http://localhost:8080/admin/v2/brokers/health

# 查看 Pulsar 日志
docker-compose logs pulsar
```

### 2. 连接 MySQL 失败

```bash
# 确认 MySQL 服务正常
docker-compose ps

# 测试连接
docker exec -it mysql-offset-store mysql -upulsar_user -ppulsar_pass
```

### 3. 文件找不到

确保文件路径正确且文件存在：
```bash
ls -lh /tmp/test.log
```

### 4. 内存不足

修改 JVM 参数：
```bash
java -Xms512m -Xmx1024m -jar target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar --file /tmp/test.log
```

## 停止服务

```bash
# 停止并删除容器
docker-compose down

# 停止并删除容器及数据卷
docker-compose down -v
```

## 性能调优建议

1. **批处理大小**: 根据消息大小调整 `file.batchSize`，建议 50-200
2. **Pulsar 配置**: 关闭批处理以获得更好的可靠性
3. **MySQL 连接池**: 增加 `mysql.maxConnections` 如果需要并发处理
4. **重试策略**: 根据网络状况调整重试参数

## 日志位置

- 控制台输出: STDOUT
- 文件日志: `logs/pulsar-eos.log`
