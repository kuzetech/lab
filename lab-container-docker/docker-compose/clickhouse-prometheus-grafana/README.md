# grafana 安装 clickhouse 数据源 监控 clickhouse 慢 SQL
# grafana 使用 promethues 数据源 监控 clickhouse 状态

## 原理说明
1. clickhouse 提供 query_log 系统表，该表记录了所有执行语句的运行过程，[相关说明](https://clickhouse.com/docs/en/operations/system-tables/query_log/)
2. 可以使用 grafana 安装 clickhouse 数据源，编写 sql 过滤查询 query_log
3. 使得管理员可以在 grafana 上监控 sql 运行情况
4. promethues 可以主动啦取 clickhouse 上 9363 指标端口的数据
5. 引入 clickhouse-exporter 提供 clickhouse_table_parts_bytes、clickhouse_table_parts_count、clickhouse_table_parts_rows 三个表级别指标，exporter 需要指定 clickhouse 8123 的端口，应该也是通过查询表完成的

## 特别注意
1. 因为 query_log 和其他的系统日志表默认数据不过期，会一直叠加，因此我们在 /quickly-start/confi.xml 中配置了多处的 `<ttl>event_date + INTERVAL 30 DAY DELETE</ttl>`
2. grafana 需要安装 clickhouse 数据源，所以需要重新构建 docker 镜像

## 目录说明
1. quickly-start，是一个包含 zookeeper、clickhouse、grafana 的 docker-compose 脚本，可以快速启动环境
2. ClickHouse Queries.json，导出的 grafana dashboard 脚本，可以直接导入创建

## 快速开始
1. 需要提前安装 docker 相关环境
2. 进入 quickly-start 运行 `docker-compose up -d` 启动环境
3. 运行 `docker exec clickhouse clickhouse-client -q 'CREATE TABLE IF NOT EXISTS system.query_log_all ON CLUSTER c1s1r as system.query_log ENGINE = Distributed(c1s1r, system, query_log);'` 在节点上创建 Distribute 表，可以查询所有节点的 query_log
4. 本地访问 `localhost:3000` 访问 grafana
5. 添加 clickhouse 数据源并导入 ClickHouse Queries.json 脚本，访问地址为 `http://clickhouse:8123`
6. 添加 promethues 数据源并导入 ClickHouse_InterServer_Metrics.json 脚本，访问地址为 `http://promethues:9090`