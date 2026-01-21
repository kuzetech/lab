# 完整使用示例

本文档提供完整的端到端使用示例，帮助你快速上手 Pulsar EOS 文件处理器。

## 示例 1: 基础使用流程

### 步骤 1: 启动服务

```bash
# 使用管理脚本启动服务
./manage.sh start

# 或使用 docker-compose 直接启动
docker-compose up -d
```

### 步骤 2: 验证服务状态

```bash
# 查看所有服务状态
./manage.sh status

# 或手动检查
docker-compose ps
curl http://localhost:8080/admin/v2/brokers/health
```

### 步骤 3: 生成测试数据

```bash
# 生成 1000 行测试数据
./scripts/test-data-gen.sh 1000 /tmp/test.log

# 查看生成的文件
head -3 /tmp/test.log
wc -l /tmp/test.log
```

### 步骤 4: 运行应用

```bash
# 方式 1: 使用 run.sh 脚本（推荐）
./run.sh /tmp/test.log

# 方式 2: 直接使用 Java
java -jar target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar --file /tmp/test.log

# 方式 3: 使用 Maven
mvn exec:java -Dexec.mainClass="com.kuzetech.bigdata.pulsar.eos.Main" \
  -Dexec.args="--file /tmp/test.log"
```

### 步骤 5: 验证结果

```bash
# 查看 MySQL 中的处理记录
./manage.sh offsets

# 消费 Pulsar 消息
./manage.sh consume 10

# 查看主题统计
./manage.sh stats
```

---

## 示例 2: 断点续传测试

### 步骤 1: 生成较大的测试文件

```bash
./scripts/test-data-gen.sh 5000 /tmp/large-test.log
```

### 步骤 2: 开始处理（处理一部分后中断）

```bash
# 启动程序
./run.sh /tmp/large-test.log

# 等待处理一部分数据后，按 Ctrl+C 中断
# 你会看到类似这样的输出：
# [INFO] Progress: 100 lines processed (total: 500)
# [INFO] Progress: 100 lines processed (total: 1000)
# ^C
```

### 步骤 3: 查看已处理的进度

```bash
# 查看偏移量
./manage.sh offsets

# 或直接查询数据库
docker exec -i mysql-offset-store mysql -upulsar_user -ppulsar_pass pulsar_offset -e \
  "SELECT file_path, processed_lines, status FROM file_offsets WHERE file_path='/tmp/large-test.log';"
```

### 步骤 4: 恢复处理

```bash
# 再次运行，程序会从上次中断处继续
./run.sh /tmp/large-test.log

# 你会看到类似这样的输出：
# [INFO] Resuming from offset: 10240 (processed lines: 1000)
```

---

## 示例 3: 自定义配置

### 步骤 1: 创建自定义配置文件

```bash
cp src/main/resources/config.yaml /tmp/my-config.yaml
```

### 步骤 2: 修改配置

```bash
# 编辑配置文件
nano /tmp/my-config.yaml

# 修改以下内容：
# - topic: persistent://public/default/my-custom-topic
# - batchSize: 200
```

### 步骤 3: 使用自定义配置运行

```bash
java -jar target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar \
  --config /tmp/my-config.yaml \
  --file /tmp/test.log
```

---

## 示例 4: 命令行参数覆盖

### 覆盖主题名称

```bash
./run.sh /tmp/test.log --topic persistent://public/default/logs-dev
```

### 覆盖批处理大小

```bash
./run.sh /tmp/test.log --batch-size 50
```

### 组合多个参数

```bash
./run.sh /tmp/test.log \
  --topic persistent://public/default/app-logs \
  --batch-size 150 \
  --pulsar-url pulsar://localhost:6650
```

---

## 示例 5: 查看和分析日志

### 查看应用日志

```bash
# 实时查看应用日志文件
tail -f logs/pulsar-eos.log

# 搜索错误
grep ERROR logs/pulsar-eos.log

# 搜索特定文件的处理记录
grep "/tmp/test.log" logs/pulsar-eos.log
```

### 查看服务日志

```bash
# 查看 Pulsar 日志
./manage.sh logs pulsar

# 查看 MySQL 日志
./manage.sh logs mysql

# 查看所有日志
./manage.sh logs
```

---

## 示例 6: 监控和调试

### 查看 MySQL 数据库详情

```bash
# 连接到数据库
./manage.sh db

# 然后在 MySQL 中执行查询：
# SELECT * FROM file_offsets ORDER BY updated_at DESC;
# SELECT * FROM transaction_log ORDER BY created_at DESC LIMIT 20;
```

### 查看详细的文件处理历史

```sql
-- 查看所有文件的处理状态
SELECT 
    file_path,
    status,
    processed_lines,
    total_lines,
    CONCAT(ROUND(processed_lines / NULLIF(total_lines, 0) * 100, 2), '%') as progress,
    error_message,
    updated_at
FROM file_offsets
ORDER BY updated_at DESC;

-- 查看失败的文件
SELECT * FROM file_offsets WHERE status = 'FAILED';

-- 查看正在处理的文件
SELECT * FROM file_offsets WHERE status = 'PROCESSING';
```

### 消费和验证消息

```bash
# 消费前 20 条消息
./manage.sh consume 20

# 查看主题详细统计
./manage.sh stats

# 使用 Pulsar CLI 查看更多信息
docker exec -it pulsar-standalone bin/pulsar-admin topics list public/default
```

---

## 示例 7: 性能测试

### 步骤 1: 生成大量数据

```bash
# 生成接近 10MB 限制的文件（约 50,000 行）
./scripts/test-data-gen.sh 50000 /tmp/perf-test.log

# 查看文件大小
ls -lh /tmp/perf-test.log
```

### 步骤 2: 运行性能测试

```bash
# 记录开始时间并运行
time ./run.sh /tmp/perf-test.log --batch-size 500
```

### 步骤 3: 分析性能

```bash
# 查看处理速度
# 计算: 总行数 / 总耗时 = 每秒处理行数

# 查看 Pulsar 主题统计
./manage.sh stats
```

---

## 示例 8: 错误处理和恢复

### 模拟错误场景

```bash
# 1. 启动处理
./run.sh /tmp/test.log

# 2. 在处理过程中停止 Pulsar
docker-compose stop pulsar

# 3. 观察应用的重试行为
# 你会看到类似的重试日志：
# [WARN] Failed to send batch (attempt 1/3), retrying in 1000ms...
# [WARN] Failed to send batch (attempt 2/3), retrying in 2000ms...
```

### 恢复服务

```bash
# 重启 Pulsar
docker-compose start pulsar

# 等待 Pulsar 就绪
sleep 10

# 应用会自动恢复发送
```

---

## 示例 9: 清理和重置

### 清理所有数据

```bash
# 停止服务并删除所有数据
./manage.sh clean

# 或手动执行
docker-compose down -v
rm -rf target/
rm -rf logs/
```

### 重新开始

```bash
# 重新启动
./manage.sh start

# 生成新的测试数据
./scripts/test-data-gen.sh 1000 /tmp/test.log

# 运行
./run.sh /tmp/test.log
```

---

## 示例 10: 批量处理多个文件

虽然当前版本一次只能处理一个文件，但你可以使用脚本批量处理：

```bash
#!/bin/bash

# 批量处理脚本
for file in /tmp/*.log; do
    echo "Processing $file..."
    ./run.sh "$file"
    
    if [ $? -eq 0 ]; then
        echo "✓ Successfully processed $file"
    else
        echo "✗ Failed to process $file"
    fi
done
```

---

## 常用命令速查表

| 操作 | 命令 |
|------|------|
| 启动服务 | `./manage.sh start` |
| 停止服务 | `./manage.sh stop` |
| 查看状态 | `./manage.sh status` |
| 生成测试数据 | `./scripts/test-data-gen.sh 1000 /tmp/test.log` |
| 运行应用 | `./run.sh /tmp/test.log` |
| 查看偏移量 | `./manage.sh offsets` |
| 消费消息 | `./manage.sh consume 10` |
| 查看日志 | `./manage.sh logs` |
| 清理数据 | `./manage.sh clean` |
| 连接数据库 | `./manage.sh db` |

---

## 故障排除

### 问题 1: 无法连接到 Pulsar

```bash
# 检查 Pulsar 是否运行
docker-compose ps pulsar

# 检查 Pulsar 日志
docker-compose logs pulsar

# 测试连接
curl http://localhost:8080/admin/v2/brokers/health
```

### 问题 2: MySQL 连接失败

```bash
# 检查 MySQL 是否运行
docker-compose ps mysql

# 测试连接
docker exec -it mysql-offset-store mysql -upulsar_user -ppulsar_pass -e "SELECT 1"
```

### 问题 3: 文件未找到

```bash
# 确认文件存在
ls -lh /tmp/test.log

# 确认文件格式正确（每行是 JSON）
head -1 /tmp/test.log | python -m json.tool
```

### 问题 4: 内存不足

```bash
# 增加 JVM 内存
java -Xms512m -Xmx2048m -jar target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar --file /tmp/test.log
```

---

## 下一步

- 阅读 [QUICKSTART.md](QUICKSTART.md) 了解更多详细信息
- 查看 [IMPLEMENTATION.md](IMPLEMENTATION.md) 了解项目实现细节
- 查看源代码了解内部工作原理
