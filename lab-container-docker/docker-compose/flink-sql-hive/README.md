## Flink SQL CLient 使用 HiveCatelog DEMO

### 快速开始
1. 在目录下使用 `docker-compose up -d` 启动一个 hdfs、hive、kafka 本地测试集群
2. 下载 flink-1.14.2 版本，解压，`./bin/start-cluster.sh` 启动一个 flink 集群
3. 根据目录下的 flink-jar-dep.jpg 下载相应的 jar 包到flink lib 目录中
4. 修改目录下的 sql-init.sql 文件中的 hive-conf-dir 属性，需要指向 flink-hive-conf 目录的绝对路径
5. 可以通过 `docker exec -it flink-table-api-hive-catelog-test_broker_1 /opt/bitnami/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test` 向 kafka 的 test topic 写入数据，数据样式如下： `tom,1`
6. 运行 link-1.14.2 的 `./bin/sql-client.sh -i ${path}/sql-init.sql`，会自动初始化表环境
7. 在 sql client 中运行 `select * from mykafka;` 可以获取刚才写入 kafka 的数据
8. 可以通过 `docker exec -it hive_hive-server_1 /opt/hive/bin/beeline -u jdbc:hive2://hive-server:10000 hive hive` 连接上 hive，并通过 `show create table mykafka;` 可以查看 flink 存储在 hive 中的元数据结构

### 实验结果
1. hive 中的元数据变更之后，flink 无法自动获取，还是需要重启应用
2. flink sql 当前无法修改表结构，只能删除后重建（该动作对使用了旧结构的其他应用不会有影响）
3. 无论是缺少字段还是增加字段，format 都会抛出异常导致程序挂掉
4. 考虑到第三点，可以添加类似 `'csv.ignore-parse-errors' = 'true'` 的参数，这样在新增字段的时候会将缺失的字段使用 null 进行填充。但是会出现有新字段的数据，旧的应用会自动丢弃
5. flink 在 hive 上支持两种表，通用表（仅存储表结构元数据，hive 仅能查看表创建语句，其他无法操作），hive 兼容表（跟hive 表一样，hive 可以写入数据也可以操作表结构）

### 总结
目前想使用 hive metastore 统一管理 flink 的元数据没办法做到开箱即用