# study-spark

## 重要网站
1. [spark 配置项](https://spark.apache.org/docs/latest/configuration.html)

## 目录说明
1. quickly，是一个包含 zookeeper、clickhouse、kafka、redis、spark standalone 集群 的 docker-compose 脚本，可以快速启动环境

## 快速开始
1. 需要提前安装 docker 相关环境
2. 需要在项目中执行 `mvn package` 的命令，生成程序 jar 包，之后会自动映射到 docker 集群中
3. 进入 quickly 文件夹执行 `docker-compose up -d` 启动环境
4. 创建数据源 topic event，命令行运行 `docker exec broker /opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic event --partitions 3 --replication-factor 1`
5. 执行 `docker exec -it master sh` 进入 spark standalone 集群 master 节点，然后执行 `chmod +x /commit.sh` 给予 spark-submit 脚本执行权限，然后执行 `/commit.sh` 提交任务到 spark 集群中
6. 手动写入数据到 topic event 中，`docker exec -it broker /opt/bitnami/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic event` ，
   写入的数据必须类似 `{"eventId":"user-login","eventTime":"2022-01-01","uid":"1"}`
7. 查看数据入库情况，命令行运行 `docker exec -it clickhouse1 clickhouse-client` 进入 clickhouse 命令行工具，执行 `select * from event_all` 查看写入的事件数据



