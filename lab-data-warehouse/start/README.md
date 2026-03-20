# HDFS HA (2 NameNode + 3 DataNode) on Docker Compose

## 架构
- 2 x NameNode (`nn1`, `nn2`) + ZKFC 自动故障切换
- 3 x JournalNode (`jn1`, `jn2`, `jn3`)
- 3 x DataNode (`dn1`, `dn2`, `dn3`)
- 3 x ZooKeeper (`zk1`, `zk2`, `zk3`)

## 启动
```bash
cd start
docker compose up -d
```

首次启动时：
- `nn1` 会自动 `-format` 并 `hdfs zkfc -formatZK`
- `nn2` 会自动 `-bootstrapStandby`

## 访问
- NameNode UI (active/standby 之一): `http://localhost:9870`
- 另一个 NameNode UI: `http://localhost:9871`

## 验证
```bash
# 查看 NameNode 状态
docker exec -it hdfs-nn1 hdfs haadmin -getServiceState nn1
docker exec -it hdfs-nn1 hdfs haadmin -getServiceState nn2

# 查看 DataNode 数量（应为 3）
docker exec -it hdfs-nn1 hdfs dfsadmin -report | grep "Live datanodes"

# 通过 nameservice 写入文件（自动走 active NameNode）
docker exec -it hdfs-nn1 hdfs dfs -mkdir -p /tmp/test
docker exec -it hdfs-nn1 bash -lc 'echo hello-ha > /tmp/hello.txt'
docker exec -it hdfs-nn1 hdfs dfs -put -f /tmp/hello.txt /tmp/test/
```

## 关闭
```bash
docker compose down
```

## 清理所有数据卷（重置集群）
```bash
docker compose down -v
```

## datagen + 双级 Flume 采集到 HDFS（HA nameservice）

### 目标链路
- `datagen` 持续生成本地日志：`/datagen/data/log/app.access.log.*`
- `flume-l1` 使用 `TAILDIR` 采集日志并通过 `AVRO` 转发
- `flume-l2` 通过 `AVRO` 接收并写入 `hdfs://mycluster/tmp/flume/datagen/%Y-%m-%d/%H`

### 启动
```bash
cd start
docker compose build flume-l1 flume-l2
docker compose up -d datagen flume-l2 flume-l1
```

### 实时观察
```bash
docker compose logs -f datagen flume-l1 flume-l2
```

### 验证 HDFS 落地
```bash
docker exec -it hdfs-nn1 hdfs dfs -ls -R /tmp/flume/datagen
```

### 验证 nameservice 写入（不是直连 nn1）
```bash
docker compose logs --tail=200 flume-l2 | grep "Creating hdfs://mycluster" | tail
```

### 语义说明（重要）
- 当前链路：`datagen -> Flume TAILDIR -> Flume AVRO -> HDFS`
- 该链路可做到“至少一次（At-Least-Once）”，不能保证严格“恰好一次（EOS）”
- 已做加固：
  - `flume-l1`、`flume-l2` 都使用 `file channel`
  - Flume 状态目录挂载到宿主机，容器重建不丢 checkpoint
  - `TAILDIR` 开启 `file + byteoffset` 头，便于下游去重
- 如果业务需要严格 EOS，建议改为：
  - 上游写 Kafka（幂等生产 + 事务）
  - 下游用 Spark/Flink 事务性写入 Iceberg/Hudi/Delta（checkpoint + commit）

## Hive 服务（Metastore + HiveServer2）

### 新增组件
- `hive-mysql`：Hive Metastore 元数据库（MySQL 8）
- `hive-metastore`：Hive Metastore（Thrift 9083，外置 MySQL 元数据库）
- `hive-server2`：HiveServer2（JDBC 10000，WebUI 10002）

### 启动
```bash
cd start
docker compose up -d hive-mysql hive-metastore hive-server2
```

### 验证服务
```bash
# 查看 Hive 服务日志
docker compose logs -f hive-mysql hive-metastore hive-server2

# 通过 beeline 连接 HiveServer2 并建表
docker exec -it hive-server2 beeline -u jdbc:hive2://localhost:10000 -n hive -e "show databases;"
```

### 验证 warehouse 在 HDFS HA nameservice
```bash
docker exec -it hdfs-nn1 hdfs dfs -ls /user/hive
```

### 端口
- Hive Metastore Thrift: `localhost:9083`
- HiveServer2 JDBC: `localhost:10000`
- HiveServer2 Web UI: `http://localhost:10002`

### 元数据库凭据（测试环境）
- Host: `hive-mysql`
- Port: `3306`
- Database: `metastore`
- Username: `hive`
- Password: `hive123`

### MySQL JDBC 驱动（Hive 连接必需）
- 挂载路径：`start/hive-lib/mysql-connector-j-8.0.33.jar`
- 若缺失可下载：
```bash
mkdir -p start/hive-lib
curl -fL --retry 3 -o start/hive-lib/mysql-connector-j-8.0.33.jar \
  https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
```
