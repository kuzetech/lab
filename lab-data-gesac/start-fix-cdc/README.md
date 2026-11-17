# Flink CDC 3.7：修复 DBZ-1682 的容器化构建

此目录构建 Flink CDC 3.7 的 PostgreSQL source connector、可部署的 SQL connector 及
Pipeline connector：
`flink-connector-postgres-cdc-3.7-SNAPSHOT.jar` 和
`flink-sql-connector-postgres-cdc-3.7-SNAPSHOT.jar`，以及
`flink-cdc-pipeline-connector-postgres-3.7-SNAPSHOT.jar`。

Flink CDC 3.7 使用 Debezium `1.9.8.Final`；该版本的
`PgOutputMessageDecoder.readString()` 会将 pgoutput 中的每个字节单独转换为
`char`，因此会损坏中文、日文、emoji 等 UTF-8 多字节表名/列名。本构建保留
Flink CDC 的依赖版本，仅为 Debezium PostgreSQL connector 回移
[DBZ-1682](https://github.com/debezium/dbz/issues/1682) 的 UTF-8 解码修复，随后
重新编译 Flink connector。

## 构建

要求：Docker Desktop（已启用 Docker Compose）。建议至少预留 8 GB Docker 内存。

```bash
cd start-fix-cdc
HOST_UID=$(id -u) HOST_GID=$(id -g) docker compose run --rm builder
```

首次运行会下载 Maven 依赖并编译，耗时较长；`maven-repository` Docker volume 会
保留依赖缓存。构建通过公共 Maven 镜像解析依赖，避免 Debezium 旧版 POM 中多个
仓库的无效探测；构建会跳过测试及测试编译。完成后产物在 `output/`：

- `flink-connector-postgres-cdc-3.7-SNAPSHOT.jar`
- `flink-sql-connector-postgres-cdc-3.7-SNAPSHOT.jar`（可直接放到 Flink `lib/`）
- `flink-cdc-pipeline-connector-postgres-3.7-SNAPSHOT.jar`（用于 Flink CDC Pipeline）
- 对应的 `.sha256` 校验文件
- `BUILD-INFO.txt`（固定源码提交和补丁说明）

构建还移除了 Debezium Core 中仅用于测试的 Confluent Avro converter 依赖；该依赖不在
connector 的运行时 classpath 中，避免因其不在公共 Maven 镜像而阻断本地修补包编译。

如需从头下载依赖并重新构建：

```bash
docker compose down -v
docker compose build --no-cache
HOST_UID=$(id -u) HOST_GID=$(id -g) docker compose run --rm builder
```

## 端到端验证

构建成功后，启动 PostgreSQL 15、Flink 1.20.3 集群并运行自动验证：

```bash
./e2e/verify.sh
```

脚本会创建包含中文、emoji 表/列名的 PostgreSQL 表，先验证两条快照记录，再插入一条
流式记录，并从 Flink TaskManager 的 `print` sink 日志检查三个 UTF-8 值。全部通过时会
输出 `PASS`。测试结束后仅删除本测试创建的容器、网络和 PostgreSQL volume。

如需保留环境以查看 Flink Web UI（http://localhost:8081）或日志：

```bash
docker compose -f compose.e2e.yaml up -d
docker compose -f compose.e2e.yaml logs -f taskmanager
```

完成后清理：

```bash
docker compose -f compose.e2e.yaml down -v
```

## 部署

SQL/Table API 场景将 `flink-sql-connector-postgres-cdc-3.7-SNAPSHOT.jar` 放入运行
Flink 的 `lib/` 目录，并移除旧版 PostgreSQL CDC SQL connector JAR，随后重启集群。
请不要同时保留两个版本；DataStream API 则使用对应的
`flink-connector-postgres-cdc-3.7-SNAPSHOT.jar`。

Flink CDC Pipeline 场景将
`flink-cdc-pipeline-connector-postgres-3.7-SNAPSHOT.jar` 放入 Pipeline 运行时的
`lib/` 目录。它在构建时依赖同一份已修补的 PostgreSQL source connector，因此也包含
DBZ-1682 的 UTF-8 pgoutput 解码修复。

在 PostgreSQL 中用 `pgoutput` 创建包含中文、日文或 emoji 标识符的 schema/table，
并在快照完成后再执行一条 DML。流式阶段的表名、列名应保持原始 UTF-8 文本；这是该
问题的验证重点。

## 固定版本

- Flink CDC `release-3.7`：`1d89ff85ebc337650ef26c3e307957657bda7db4`
- Debezium `v1.9.8.Final`：`c88a467e91a7acd0dd535c076a337439e23147ed`

可在 `.env` 中覆盖 `FLINK_CDC_COMMIT` 与 `DEBEZIUM_COMMIT`，但补丁仅保证上述版本
能够干净应用。
