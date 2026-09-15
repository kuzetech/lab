# Docker Quick Start

这个目录提供一个可直接运行的 MySQL + Kafka + HDFS + YARN + Flink SQL + Hive Docker 环境。

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
docker compose logs -f hdfs-namenode
docker compose logs -f hdfs-datanode
docker compose logs -f yarn-resourcemanager
docker compose logs -f yarn-nodemanager
docker compose logs -f flink-sql-client
docker compose logs -f hive-metastore
docker compose logs -f hive-server2
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

## HDFS

### 集群配置

- Hadoop 镜像：`apache/hadoop:3.3.6`
- NameNode：1 个
- DataNode：1 个
- 副本数：`1`

### 连接地址

- NameNode Web UI：`http://localhost:9870`
- DataNode Web UI：`http://localhost:9864`
- HDFS 地址：`hdfs://localhost:9000`
- Compose 网络内 HDFS 地址：`hdfs://hdfs-namenode:9000`

### 常用命令

查看 HDFS 状态：

```bash
docker compose exec hdfs-namenode hdfs dfsadmin -report
```

创建目录：

```bash
docker compose exec hdfs-namenode hdfs dfs -mkdir -p /tmp
```

上传文件：

```bash
docker compose exec hdfs-namenode bash -lc 'echo hello-hdfs > /tmp/hdfs-demo.txt && hdfs dfs -put -f /tmp/hdfs-demo.txt /tmp/'
```

查看文件：

```bash
docker compose exec hdfs-namenode hdfs dfs -ls /tmp
```

## YARN

### 集群配置

- Hadoop 镜像：`apache/hadoop:3.3.6`
- ResourceManager：1 个
- NodeManager：1 个
- NodeManager 资源：`2` vcores、`2048 MB` 内存

### 连接地址

- ResourceManager Web UI：`http://localhost:8088`
- NodeManager Web UI：`http://localhost:8042`
- Compose 网络内 ResourceManager：`yarn-resourcemanager:8032`

### 常用命令

查看节点：

```bash
docker compose exec yarn-resourcemanager yarn node -list
```

查看应用：

```bash
docker compose exec yarn-resourcemanager yarn application -list
```

## Flink SQL

### 服务配置

- Flink 镜像：`flink:1.17.2-scala_2.12-java11`
- 服务名：`flink-sql-client`
- 执行目标：`yarn-per-job`
- Hadoop client：由 `hadoop-client-init` 使用 `apache/hadoop:3.3.6` 初始化到共享卷
- YARN executor：由 `flink-yarn-init` 下载 `flink-yarn-1.17.2.jar` 后挂载到 Flink
- Hive Catalog：由 `flink-hive-init` 下载 `flink-sql-connector-hive-3.1.3_2.12-1.17.2.jar` 后挂载到 Flink
- YARN ResourceManager：`yarn-resourcemanager:8032`
- HDFS：`hdfs://hdfs-namenode:9000`
- Hive Metastore：`hive-metastore:9083`

### 常用命令

进入 SQL Client：

```bash
docker compose exec flink-sql-client sql-client-yarn.sh
```

提交 SQL 文件到 YARN：

```bash
docker compose exec flink-sql-client sql-client-yarn.sh -f /opt/flink/sql/example.sql
```

验证 Hive Catalog：

```bash
docker compose exec flink-sql-client sql-client-yarn.sh -f /opt/flink/sql/hive-catalog.sql
```

查看 YARN 应用：

```bash
docker compose exec yarn-resourcemanager yarn application -list
```

## Hive

### 服务配置

- Hive 镜像：`apache/hive:3.1.3`
- Metastore：`hive-metastore:9083`
- HiveServer2：`hive-server2:10000`
- HiveServer2 Web UI：`http://localhost:10002`
- Metastore 存储：现有 `mysql` 服务中的 `hive_metastore` 数据库
- Warehouse：`hdfs://hdfs-namenode:9000/user/hive/warehouse`

### MySQL Connector

Hive 通过 MySQL Connector/J 连接 metastore 数据库，默认挂载：

```text
../../lab-data-warehouse/start/hive-lib/mysql-connector-j-8.0.33.jar
```

如需改用其他 jar，可修改 `.env` 中的 `HIVE_MYSQL_CONNECTOR_JAR`。

### 常用命令

连接 HiveServer2：

```bash
docker compose exec hive-server2 beeline -u jdbc:hive2://localhost:10000 -n hive
```

查看数据库：

```bash
docker compose exec hive-server2 beeline -u jdbc:hive2://localhost:10000 -n hive -e 'show databases;'
```

查看 metastore 表：

```bash
docker compose exec mysql mysql -uhive -phive_password hive_metastore -e 'SHOW TABLES;'
```

## 停止

```bash
docker compose down
```
