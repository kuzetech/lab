## 官网地址

https://airflow.apache.org/docs/apache-airflow/stable/tutorial.html#

## 执行步骤

1. 执行 `docker-compose up -d` 启动所有基础服务
2. 执行 `docker-compose --profile flower up -d` 启动监控服务
3. 执行 `docker-compose --profile clickhouse up -d` 启动 clickhouse 服务
4. 可以通过 `http://localhost:8080` 访问操作界面，默认的用户和密码都是 airflow
5. 可以通过 `http://localhost:5555/` 访问监控系统

## 命令行模式测试运行

1. 执行 `docker exec -it scheduler-airflow_airflow-worker_1 sh` 进入命令行，可以是 scheduler、worker、webserver 节点
2. 执行 `airflow tasks test [dag_id] [task_id] [logical_date]`，logical_date 的作用是模拟该任务执行的时间，例如 2015-06-01。命令在本地运行任务实例，将其日志输出到标准输出(在屏幕上) ，不考虑依赖关系，并且不将状态(运行、成功、失败、 ...)传递给数据库。只允许测试单个任务实例
3. 执行 `airflow dags test [dag_id] [logical_date]`，logical_date 的作用是模拟该任务执行的时间，例如 2015-06-01。该命令会完整的执行一次 DAG 运行，但是数据库中没有注册任何状态
4. 执行 `airflow dags backfill [dag_id] --start-date 2015-06-01 --end-date 2015-06-07`，生成从开始时间到结束时间的多个调度任务，实际执行调度依赖，将记录日志并更新数据库任务状态

## 命令行安装 provider package

1. 可以在 [这里](https://airflow.apache.org/docs/apache-airflow-providers/packages-ref.html) 查看可以安装的包
2. 执行 `pip install apache-airflow-providers-jdbc` 安装对应的包
3. 想通过 jdbc 访问 clickhouse，但是安装相应的 provider 后，web 界面并没有多出 jdbc 的数据源可选
4. 还没有尝试通过 python 代码定义 dag 的方式能否使用 jdbc
