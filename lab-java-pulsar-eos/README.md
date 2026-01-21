## 项目目的
本项目旨在读取用户指定的文件，并将文件内容发送到 Apache Pulsar 的指定主题中

## 项目依赖
1. java 11
2. maven 3.9
3. docker compose
4. pulsar 4.0.1
5. mysql 8.0

## 功能特性
1. 用户可以通过配置文件或命令行参数指定文件路径、Pulsar 连接信息和目标主题
2. 读取文件格式为日志文件，以 .log 结尾，每一行为一个 json 对象，文件大小不超过 10MB
3. **通过事务保证消息发送的原子性和一致性，实现仅一次投递（Exactly Once Delivery）**
4. 通过 mysql 记录已发送的消息偏移量，支持断点续传功能
5. 支持错误处理和重试机制，确保消息发送的可靠性
6. 提供详细的日志记录，方便用户进行问题排查和性能分析

## 技术亮点

### 1. ✅ EOS 事务语义
- 使用 Pulsar Transaction API 实现 Exactly-Once Semantics
- 每个批次在独立事务中发送，保证原子性
- 事务失败自动回滚，确保消息不重复不丢失
- 完整的事务状态追踪（STARTED, COMMITTED, ABORTED）

### 2. ✅ Lombok 简化代码
- 使用 `@Data`, `@Slf4j`, `@Builder` 等注解
- 减少 50% 的模板代码
- 代码更简洁、易维护

### 3. ✅ MyBatis ORM 框架
- 替代原始 JDBC 代码
- 自动对象关系映射
- 类型安全的 DAO 层
- 连接池管理

### 4. ✅ Docker 容器化
- 多阶段构建优化镜像大小
- 完整的 Docker 支持
- 一键构建和部署

## 代码细节
1. 使用 **Lombok** 简化 Java 代码编写
2. 使用 **MyBatis** 进行数据库访问
3. 使用 **Pulsar Transaction API** 实现 EOS 语义
4. 使用 **Docker** 多阶段构建容器镜像

## 快速测试

### 方式 1: 传统方式
1. 通过项目下的 [docker-compose.yml](docker-compose.yml) 文件启动 Pulsar 和 MySQL 服务
   ```bash
   docker-compose up -d
   ```

2. 通过项目下的 [test-data-gen.sh](scripts/test-data-gen.sh) 脚本生成测试数据文件
   ```bash
   chmod +x scripts/test-data-gen.sh
   ./scripts/test-data-gen.sh 1000 /tmp/test.log
   ```

3. 编译并打包项目
   ```bash
   mvn clean package
   ```

4. 运行程序
   ```bash
   java -jar target/lab-java-pulsar-eos-1.0-SNAPSHOT.jar --file /tmp/test.log
   ```

### 方式 2: Docker 方式

1. 启动服务
   ```bash
   docker-compose up -d
   ```

2. 构建 Docker 镜像
   ```bash
   ./docker-build.sh build
   ```

3. 运行容器
   ```bash
   ./docker-build.sh run /tmp/test.log
   ```

## 验证事务功能

```bash
# 查看事务日志
./manage.sh db

# 在 MySQL 中执行
SELECT * FROM transaction_log ORDER BY created_at DESC LIMIT 10;

# 查看事务状态分布
SELECT status, COUNT(*) as count FROM transaction_log GROUP BY status;
```

## 详细文档

- [快速开始指南](QUICKSTART.md) - 详细的使用说明和示例
- [使用示例](EXAMPLES.md) - 10+ 完整使用场景
- [优化总结](OPTIMIZATION.md) - 代码优化详细说明
- [实现总结](IMPLEMENTATION.md) - 项目实现细节
- [配置说明](src/main/resources/config.yaml) - 配置文件模板和参数说明
- [数据库设计](scripts/init-db.sql) - MySQL 表结构说明

## 项目结构

```
lab-java-pulsar-eos/
├── src/main/java/
│   └── com/kuzetech/bigdata/pulsar/eos/
│       ├── Main.java                      # 主程序入口
│       ├── config/
│       │   ├── AppConfig.java             # 配置类（Lombok）
│       │   └── ConfigLoader.java          # 配置加载器
│       ├── entity/
│       │   ├── FileOffset.java            # 文件偏移量实体（Lombok + MyBatis）
│       │   └── TransactionLog.java        # 事务日志实体（Lombok + MyBatis）
│       ├── mapper/
│       │   ├── FileOffsetMapper.java      # MyBatis Mapper
│       │   └── TransactionLogMapper.java  # MyBatis Mapper
│       ├── manager/
│       │   ├── OffsetManager.java         # 偏移量管理器（MyBatis）
│       │   └── PulsarProducerManager.java # Pulsar 生产者（支持事务）
│       └── processor/
│           └── FileProcessor.java         # 文件处理器（EOS 事务）
├── src/main/resources/
│   ├── config.yaml                        # 配置文件模板
│   ├── logback.xml                        # 日志配置
│   └── mybatis-config.xml                 # MyBatis 配置
├── scripts/
│   ├── init-db.sql                        # MySQL 初始化脚本
│   └── test-data-gen.sh                   # 测试数据生成脚本
├── Dockerfile                             # Docker 镜像构建文件
├── docker-build.sh                        # Docker 构建脚本
├── docker-compose.yml                     # Docker 服务编排
├── pom.xml                                # Maven 配置
└── README.md                              # 项目说明
```

## 开发指南

### 编译项目
```bash
mvn clean compile
```

### 运行测试
```bash
mvn test
```

### 打包项目
```bash
mvn clean package
```

### 构建 Docker 镜像
```bash
./docker-build.sh build
```

## 性能特性

- **EOS 保证**: Exactly-Once Semantics，消息不重复不丢失
- **断点续传**: 基于 MySQL 偏移量管理，支持中断恢复
- **批量处理**: 可配置批次大小，提升吞吐量
- **重试机制**: 指数退避重试策略，提高成功率
- **事务隔离**: 每批次独立事务，失败不影响已提交数据

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## License

Apache License 2.0
