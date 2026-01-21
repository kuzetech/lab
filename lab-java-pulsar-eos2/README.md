## 项目目的
本项目旨在读取用户指定的文件，并 将文件内容发送到 Apache Pulsar 的指定主题中

## 项目依赖
- java 11
- maven 3.9
- pulsar 4.0.1

## 功能特性
- 用户可以通过配置文件或命令行参数指定文件路径、Pulsar 连接信息和目标主题
- 读取文件格式为日志文件，以 .log 结尾，文件大小不超过 10MB
- 读取文件内容，当累计到一定的行数时，通过事务将批次数据发送到 Pulsar
- 通过文件记录已发送的消息偏移量，支持断点续传功能
- 结合 pulsar 事务和文件偏移量，实现数据不丢失、不重复的 Exactly Once Delivery
- 支持错误处理和重试机制，确保消息发送的可靠性
- 提供详细的日志记录，方便用户进行问题排查和性能分析

## 代码实现要求
- 请不要修改本文件
- 使用 Maven 进行项目管理
- 使用 Lombok 简化 Java 代码编写
- 脚本都放置在 [scripts](scripts) 目录下
- 在 [Dockerfile](Dockerfile) 中实现多阶段构建容器镜像
- 在 [run.sh](scripts%2Frun.sh) 中实现项目快速测试


## 技术方案
### EOS 事务实现
- 创建事务后，先将本批次的起始偏移量、结束偏移量，事务id，及事务状态 pending 写入本地文件
- 发送消息到 Pulsar 主题
- 提交 Pulsar 事务
- 更新本地文件中该事务状态为 committed
- 如果事务失败自动回滚，确保消息不重复不丢失
- 如果程序异常导致重启，启动时检查本地文件中未完成的事务，根据事务id 向 pulsar 服务查询事务状态
  - 如果状态为 committed，则更新本地文件中该事务状态为 committed，并将偏移量更新到该事务的结束偏移量
  - 如果状态为 aborted，则回滚本地文件中的偏移量到该事务的起始偏移量
  - 其他状态下，保守处理，回滚本地文件中的偏移量到该事务的起始偏移量，并开启一个新的事务重新发送该批次数据

## 快速测试
- 使用 [test-data-gen.sh](scripts%2Ftest-data-gen.sh) 脚本生成测试数据文件
- 使用 [docker-compose.yml](docker-compose.yml) 快速启动测试环境
- 使用 [config.yaml](src%2Fmain%2Fresources%2Fconfig.yaml) 存放项目默认配置
- 运行 [Main.java](src%2Fmain%2Fjava%2Fcom%2Fkuzetech%2Fbigdata%2Fpulsar%2Feos%2FMain.java) 主程序快速测试
