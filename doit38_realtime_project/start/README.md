# Doris Docker Compose

这个目录提供了一个适合本地联调的 Apache Doris 测试集群：

- `1 FE + 1 BE`
- FE Web UI: `http://127.0.0.1:8030`
- FE MySQL 协议端口: `127.0.0.1:9030`
- BE Web UI: `http://127.0.0.1:8040`

## 启动

在仓库根目录执行：

```bash
docker compose -f start/docker-compose.yml up -d
```

## 停止

```bash
docker compose -f start/docker-compose.yml down
```

如果想连同本地测试数据一起删除：

```bash
docker compose -f start/docker-compose.yml down -v
rm -rf start/doris-data
```

## 连接 Doris

```bash
mysql -h 127.0.0.1 -P 9030 -uroot
```

首次启动通常需要等待几十秒到几分钟，等 FE 和 BE 都起来之后再连。

可以用下面两条命令检查状态：

```bash
mysql -h 127.0.0.1 -P 9030 -uroot -e "show frontends;"
mysql -h 127.0.0.1 -P 9030 -uroot -e "show backends;"
```

## 说明

- 这里使用的是 Doris 官方 Docker Hub 运行时镜像标签：`apache/doris:fe-4.0.1` 和 `apache/doris:be-4.0.1`。
- Doris 官方 Docker Hub 说明里写明这些运行时镜像同时支持 `amd64` 和 `arm64`，所以这里不再强制做架构模拟。
- 这是开发测试集群，不建议直接当生产方案使用。
