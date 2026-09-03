# Docker Quick Start

这个目录提供一个可直接运行的 MySQL + Kafka + Doris Docker 环境。

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
docker compose logs -f doris
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
- root 密码：见 `.env` 中的 `MYSQL_ROOT_PASSWORD`
- 应用库：见 `.env` 中的 `MYSQL_DATABASE`
- 应用用户：见 `.env` 中的 `MYSQL_USER`
- 应用密码：见 `.env` 中的 `MYSQL_PASSWORD`

### 初始化脚本

首次创建数据卷时，`initdb/` 目录下的 `.sql` 文件会自动执行。数据库一旦初始化完成，后续重启不会重复执行这些脚本。

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

## Doris

### 连接地址

- FE Web UI：`http://localhost:8030`
- MySQL 协议：`localhost:9030`
- BE Web UI：`http://localhost:8040`

### 连接命令

```bash
mysql -h127.0.0.1 -P9030 -uroot
```

查看集群：

```bash
mysql -h127.0.0.1 -P9030 -uroot -e 'SHOW BACKENDS;'
```

## 停止

```bash
docker compose down
```
