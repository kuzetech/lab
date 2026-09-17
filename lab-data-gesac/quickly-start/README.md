# Docker Quick Start

这个目录提供一个可直接运行的 MySQL + Kafka + Hive Metastore Docker 环境。

## 启动

```bash
cd quickly-start
docker compose up -d
```

## 查看状态

```bash
docker compose ps
docker compose logs -f mysql
docker compose logs -f kafka
docker compose logs -f hive-metastore
```

## MySQL

### 连接数据库

使用容器内客户端：

```bash
docker compose exec mysql mysql -uapp_user -papp_password app_db
```

从宿主机连接：

```bash
mysql -h127.0.0.1 -P3306 -uapp_user -papp_password app_db
```

## 账号

- root 用户：`root`
- root 密码：`root_password`
- 应用库：`app_db`
- 应用用户：`app_user`
- 应用密码：`app_password`
- Hive Metastore 库：`hive_metastore`
- Hive Metastore 用户：`hive`
- Hive Metastore 密码：`hive_password`

### 初始化脚本

首次创建数据卷时，`mysql/initdb/` 目录下的 `.sql` 文件会自动执行。数据库一旦初始化完成，后续重启不会重复执行这些脚本。

如需重新初始化：

```bash
docker compose down -v
docker compose up -d
```

## Kafka

### 连接地址

- 宿主机访问：`localhost:9092`
- Compose 网络内访问：`kafka:29092`

### 常用命令

创建 topic：

```bash
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic quickstart-events --partitions 3 --replication-factor 1
```

查看 topic：

```bash
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

生产消息：

```bash
docker compose exec kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic quickstart-events
```

消费消息：

```bash
docker compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic quickstart-events --from-beginning
```

## Hive Metastore

Hive Metastore 使用 `apache/hive:3.1.3` 镜像，以 standalone metastore 模式运行，并连接到 MySQL 中的 `hive_metastore` 数据库。

### MySQL JDBC Driver

`apache/hive:3.1.3` 镜像不内置 MySQL Connector/J。默认使用这个目录中的 MySQL JDBC driver：

```text
quickly-start/mysql/jar/mysql-connector-j-8.0.33.jar
```

也可以通过环境变量指定本机已有的 jar：

```bash
MYSQL_CONNECTOR_JAR=/path/to/mysql-connector-j.jar docker compose up -d hive-metastore
```

### 连接地址

- 宿主机访问：`thrift://localhost:9083`
- Compose 网络内访问：`thrift://hive-metastore:9083`

### Schema 初始化

容器启动时会先检查 MySQL 中是否已经存在 Hive Metastore schema：

- 首次启动：自动执行 `schematool -dbType mysql -initSchema`
- 后续启动：自动跳过 schema 初始化，直接启动 metastore

查看日志：

```bash
docker compose logs -f hive-metastore
```

## 停止

```bash
docker compose down
```
