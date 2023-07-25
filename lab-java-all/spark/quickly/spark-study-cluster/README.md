## 执行步骤

1. 执行 `docker-compose up -d` 启动集群，包含 zookeeper、kafka、spark 集群
2. 可以通过 `http://localhost:8080/` 访问 spark 集群 master 节点
3. 可以通过 `http://localhost:8081/` 访问 spark 集群 worker1 节点
4. 可以通过 `http://localhost:8082/` 访问 spark 集群 worker2 节点