## 重要说明

1. 运行 `clickhouse-client` 可以通过指定参数达到不同的效果
   - --send_logs_level=trace ，打印查询详细日志
   - --multiquery ， 一次执行多条sql
2. 除了打开客户端，还可以通过文件的方式执行，具体语句如 `clickhouse-client --send_logs_level=trace --multiquery < /sql/new.sql`