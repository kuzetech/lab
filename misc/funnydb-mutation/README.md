# Funnydb Mutation Application (Java)

## 业务背景与目标

该应用是一个“面向对象的属性变更执行器”，上游通过 Kafka 发送属性变更指令，应用将这些指令更新到 redis json 对象中，并将最新的对象快照输出到相应的 kafka topic 中。

## 技术选型

- 开发语言: java11
- 存储: Redis (启用 RedisJSON 模块)
- 计算: 轻量级 Consumer 和 Producer
- 一致性协议: Redis 时间戳幂等 + Kafka 事务提交

## 运行前提与配置约束

### 基础依赖

- Kafka 集群需要开启事务能力，Broker 版本建议不低于 2.8
- Redis 需要安装 RedisJSON 模块，用于原子读写对象文档
- 应用实例需要能够同时访问 Kafka 与 Redis，并保证时钟误差可控

### 必填配置项

- `kafka.bootstrap.servers`：Kafka 地址
- `kafka.consumer.group-id`：Consumer Group 标识
- `kafka.consumer.input-topic`：输入 topic，默认 `funnydb-ingest-mutation-events`
- `kafka.producer.transactional-id-prefix`：事务 Producer 标识前缀，要求在同一部署环境内唯一
- `redis.address`：Redis 地址
- `worker.thread-count`：工作线程数，默认 `2 x CPU`
- `tx.batch-size`：单次事务提交的消息数，默认 `200`
- `tx.flush-interval-ms`：批次未满时的最长等待提交时间
- `metrics.bind-address`：指标 HTTP 服务监听地址，默认 `0.0.0.0`
- `metrics.port`：指标 HTTP 服务端口，默认 `8080`

### Kafka 关键参数建议

- Consumer 设置 `enable.auto.commit=false`
- 下游消费者设置 `isolation.level=read_committed`，避免读取未提交事务消息
- Producer 设置 `acks=all`
- Producer 开启幂等能力，并配合事务使用

### 上游输入前置约束

- 同一对象必须始终使用同一个 Kafka message key，确保落到同一分区
- 同一对象的 `data.#time` 必须严格递增，不能重复、不能回退
- 严格保证 `#time` 递增

## 核心数据模型

### 输入消息模型

输入 topic 固定为 funnydb-ingest-mutation-events  
上游通过设置 kafka message key 确保同一对象的事件分布在相同的分区中。

输入消息定义为：
```json
{
    "type": "UserMutation",
    "data": {
        "#operate": "set",
        "#time": 1775540629881,
        "#request_id": "019d6677-f1e0-7b6e-a624-4f63f80745ea",
        "#sdk_version": "2.1.1",
        "#log_id": "019d6677-f179-7deb-9116-01e4ea03312c",
        "#sdk_type": "go-sdk",
        "#identify": "488448003",
        "properties": {
            "#zone_offset": 8,
            "hidden_score": 1956
        },
        "#ingest_host": "ingest-gaga-server.zh-cn.xmfunny.com",
        "#data_lifecycle": "0",
        "#ingest_time": 1775540629984
    },
    "ip": "8.133.195.46",
    "app": "ga_ga_she_ji_x0c11yg5",
    "key": "ga_ga_she_ji_x0c11yg5019d6677-f179-7deb-9116-01e4ea03312c",
    "logId": "019d6677-f179-7deb-9116-01e4ea03312c",
    "ingest_time": 1775540629984,
    "access_id": "FDI_7zi4vXPrEevCuRIcrhBR"
}
```

重要字段说明：
- `app`：描述对象属于哪个应用
- `type`：描述对象的类型，UserMutation 代表更新用户对象， DeviceMutation 代表更新设备对象
- `data`：描述更新内容
  - `#operate`：更新操作的类型有 `add`、`set`、`setOnce`
  - `#time`：事件时间
  - `#identify`：更新对象唯一标识
  - `#data_lifecycle`：描述对象的数据周期
  - `properties`：本次更新对象的变更属性

### 输出消息模型

输出 topic 需要根据消息内容确定，生成规则为 ${app}-flink-(users|devices)  
并且使用 #identify 作为 key，确保同一对象分配到同意分区中。

输出到 Kafka 的消息内容为“最新对象快照”，格式如下：
```json
{
    "account_create_time": 1587394795000,
    "last_login_time": 1775403965000,
    "season_period": "SS24",
    "send_candy": 307,
    "#user_id": "3kjcjn",
    "pid": 215893571,
    "nick": "左眼往左看.",
    "permit_level": 250,
    "con_count": 33,
    "game_area": "CN",
    "pay_candy": 0,
    "lv_score": 5850,
    "send_primecrate_key": 284,
    "pay_sum": 327,
    "account_chan": "[\"taptap\"]",
    "pay_wish": 0,
    "#account_id": "165725823",
    "pay_primecrate_key": 0,
    "max_score": 6011,
    "club_id": 2024776,
    "last_login_time_account": 1775399261,
    "create_time_account": 1587394795,
    "#event_time": 1775541127785,
    "#updated_time": 1775541164118,
    "#data_lifecycle": "0",
    "#created_time": 1706243697000
}
```

输出快照补充约定：

- 输出消息表示对象在当前时刻的“全量最新快照”，而不是本次事件的增量字段
- 输出内容来自 Redis 中对象的业务数据视图，部分包含 `metadata` 节点
- 输出时需要将 Redis 元数据中的 `#created_time`、`#data_lifecycle` 按原字段名写入快照数据中
- `#updated_time` 表示本次快照发送到 Kafka 的发送时间
- `#event_time` 表示最后一次成功生效事件的 `data.#time`
- 当 `type=UserMutation` 时，输出快照中补充 `#user_id=#identify`
- 当 `type=DeviceMutation` 时，输出快照中补充 `#device_id=#identify`
- 如果字段被显式设置为 `null`，则快照中保留该字段并输出 `null`
- 如果字段从未出现，则快照中不输出该字段

### Redis 数据结构设计

在 Redis 中，将每个对象存储为一个 JSON 文档。
其 Key 的生成规则为 ${app}:${#data_lifecycle}:(user|device):${#identify}

JSON 内容结构:
```json
{
  "data": {
    "uid": "1001",
    "name": "Alex",
    "score": 500,
    "age": 28,
    "vips": 1
  },
  "metadata": {
    "last_event_time": 1712461756000,
    "#created_time": 1712461756000,
    "#data_lifecycle": "0"
  }
}
```

字段约定：

- `data`：对象当前的业务属性全集
- `metadata.last_event_time`：当前对象最近一次成功生效事件的业务时间
- `metadata.#created_time`：对象首次事件的事件时间，仅在对象首次创建时写入，后续保持不变
- `metadata.#data_lifecycle`：对象首次事件的数据周期，仅在对象首次创建时写入，后续保持不变

## 操作语义设计

支持以下基础操作：

### `add`

对数值类型属性做增量累加。

示例：

- 原值：`age = 10`
- 变更：`{"#identify": 1,"#operate":"add","properties":{"age":1}}`
- 结果：`age = 11`

约束：

- 仅允许对数值类型字段执行 `add`
- 如果该字段不存在则初始化该字段为0，并执行累加操作
- 如果输入值不是数值类型，则忽略该字段更新
- 数值计算统一按 JSON number 处理，具体落库类型由实现层决定

### `set`

覆盖属性值。

示例：

- 原值：`name = 'zhangsan'`
- 变更：`{"#identify": 1,"#operate":"set","properties":{"name":"wangwu"}}`
- 结果：`name = 'wangwu'`

约束：

- 允许将已有非空字段覆盖为 `null`
- 当字段类型不匹配时，忽略操作
- 当字段不存在时，创建该字段并赋值

### `setOnce`

当字段不存在或为 `null` 时才覆盖属性值。

示例：

- 原值：`name = null`
- 变更：`{"#identify": 1,"#operate":"setOnce","properties":{"name":"wangwu"}}`
- 结果：`name = 'wangwu'`

约束：

- 当字段类型不匹配时，忽略操作
- 当字段已存在并且非空值时，不覆盖原值
- 仅将数据库中的 `null` 视为空，不把空字符串、`0`、`false` 视为空

### 类型与字段边界

- `properties` 默认仅支持一级扁平字段更新，不支持 JSONPath 或深层嵌套路径写入
- 数组与对象类型允许通过 `set` / `setOnce` 整体覆盖，不支持局部 merge
- 类型不匹配时，仅忽略当前字段，不影响同一条事件中的其他字段继续处理
- 当前版本不提供 `remove` / `unset` 操作；字段删除不在本应用职责范围内


## 一致性与幂等性设计

该应用采用“**Redis 时间戳幂等 + Kafka 事务提交**”的方式来保证处理链路的一致性。Redis 负责抵御重复事件与乱序旧事件，Kafka 负责将“发送下游快照”和“提交消费 offset”纳入同一个事务中，从而保证消费进度与下游可见结果一致。

### 一致性目标

- **单对象顺序一致**：上游使用对象标识作为 Kafka message key，确保同一对象进入同一分区；Consumer 按分区顺序处理，可认为同一对象的事件按写入顺序串行执行
- **消费进度与下游结果一致**：只有当下游快照发送成功，并且消费 offset 一并写入 Kafka 事务后，本批消息才对外可见
- **最终状态一致**：当发生进程重启、网络抖动、Kafka 重投等情况时，允许消息重复处理，但最终 Redis 中保留的对象状态应与“最后一次有效事件”一致

## 压测与可观测性

仓库已内置一套本地压测环境，包含：

- `kafka`：单节点 Kafka，开启事务能力
- `redis`：带 RedisJSON 的 Redis Stack
- `app`：待测 `funnydb-mutation` 服务
- `loadgen`：Kafka 压测消息生产器
- `prometheus`：采集应用与中间件指标
- `grafana`：预置性能看板
- `kafka-exporter` / `redis-exporter` / `kafka-ui`：辅助观测

### 一键启动基础环境

```bash
docker compose up -d --build kafka redis kafka-init app prometheus grafana kafka-exporter redis-exporter kafka-ui
```

启动后可访问：

- 应用指标：`http://localhost:8080/metrics`
- Prometheus：`http://localhost:9090`
- Grafana：`http://localhost:3000`，默认账号密码 `admin/admin`
- Kafka UI：`http://localhost:8081`

### 启动压测流量

```bash
docker compose --profile loadtest up --build loadgen
```

默认压测配置在 [docker/app/loadgen.properties](/Users/huangsw/code/funny/funnydb/misc/funnydb-mutation/docker/app/loadgen.properties)，可直接调整：

- `load.messages`：总消息数
- `load.rate-per-second`：目标发送速率
- `load.key-cardinality`：对象 key 基数
- `load.app`：应用名，同时影响下游输出 topic

服务压测配置在 [docker/app/application-perf.properties](/Users/huangsw/code/funny/funnydb/misc/funnydb-mutation/docker/app/application-perf.properties)，建议重点调：

- `worker.thread-count`
- `tx.batch-size`
- `tx.flush-interval-ms`

### 默认观测指标

应用 `metrics` 端点已输出 Prometheus 文本格式指标，重点包括：

- 吞吐：`mutation_processed_messages_total`、`mutation_published_snapshots_total`
- 延迟直方图：`mutation_message_processing_seconds`、`mutation_redis_apply_seconds`、`mutation_kafka_publish_batch_seconds`
- 批处理：`mutation_batch_snapshot_size`
- 消费行为：`mutation_consumer_polled_records_total`、`mutation_consumer_rebalances_total`
- 运行态：`mutation_jvm_memory_used_bytes`、`mutation_jvm_threads_live`

Grafana 已预置 `FunnyDB Mutation Overview` 看板，默认展示：

- 处理吞吐与快照吞吐
- 消息处理 p95/p99
- Kafka 事务提交批次延迟
- 错误消息、旧事件忽略、rebalance
- JVM 与 Redis 内存

### 本地验证建议

1. 先启动基础环境，确认 `http://localhost:8080/healthz` 返回 `ok`
2. 打开 Grafana 看板，确认 Prometheus 数据源正常
3. 启动 `loadgen`
4. 观察 `processed msg/s`、`p95/p99`、Redis 内存与 Kafka rebalance 是否稳定

### Redis 侧幂等控制

为了避免重复消费导致对象被重复修改，Redis 中除了业务数据外，还需要维护最小幂等元数据：

```json
{
  "data": {
    "uid": "1001",
    "name": "Alex",
    "score": 500
  },
  "metadata": {
    "last_event_time": 1712461756000,
    "#created_time": 1712461756000,
    "#data_lifecycle": "0"
  }
}
```

处理输入事件时，按以下规则判定是否执行：

- 当 `#time` 小于 `metadata.last_event_time`，认为是重复事件或乱序旧事件，直接忽略
- 当 `#time` 大于 `metadata.last_event_time`，执行变更，并同步更新 `last_event_time`
- 当 `#time` 等于 `metadata.last_event_time`，视为非法或重放事件，直接忽略

这样可以覆盖最常见的重复投递场景，尤其是 Consumer 在“写 Redis 成功但事务尚未提交”时发生重启的情况。

### 为什么 `add` 也能保证幂等

`set` 和 `setOnce` 本身更容易做到重复覆盖无副作用，但 `add` 属于增量操作，如果没有幂等控制，同一条消息重放会导致数值被重复累加。

因此，`add` 的幂等性不依赖“操作本身天然幂等”，而是依赖“**旧事件不能再次覆盖新状态**”。只要重放消息的 `#time` 小于或等于当前 `last_event_time`，就不会再次执行累加。

### Kafka 事务与批量提交

Kafka 侧开启事务能力，并将“发送下游快照”与“提交消费 offset”放入同一个事务中处理。

推荐按批处理，每处理 200 条消息提交一次事务，流程如下：

1. Consumer 拉取一批消息，并逐条完成 Redis 幂等判断与对象更新
2. 对于成功生效或判定为可安全忽略的消息，读取 Redis 中当前对象数据与元数据，组装最新对象快照并写入共享 Producer 的事务缓冲区
3. 当累计处理达到 200 条消息时，调用 Producer 提交事务
4. 在同一个事务内，将本批消息对应的消费 offset 一并提交
5. 事务提交成功后，再继续处理下一批数据

如果批次未满 200 条，但达到轮询超时、分区回收或进程准备关闭，也应主动提交当前事务，避免长时间悬挂未提交数据。

其中 Redis 更新阶段建议通过 **Lua Script** 或其他 Redis 原子写方式实现，避免并发下出现“先判断、后写入”之间的竞态问题。

建议 Lua 脚本返回以下结果之一，方便上层统一处理：

- `APPLIED`：事件成功生效，返回最新对象快照
- `IGNORED_OLD_EVENT`：事件因时间回退或重复被忽略，返回当前对象快照
- `INVALID_FIELD_TYPE`：部分字段类型不匹配，但事件整体可视为已处理
- `FAILED`：Redis 更新失败，需要回滚事务并等待重试

其中 Lua 脚本在对象首次创建时还应完成以下元数据初始化：

- 将当前事件的 `data.#time` 写入 `metadata.#created_time`
- 将当前事件的 `data.#data_lifecycle` 写入 `metadata.#data_lifecycle`
- 将当前事件的 `data.#time` 写入 `metadata.last_event_time`

### 故障场景说明

- **Redis 写入失败**：中止当前事务，不提交 offset，等待 Kafka 重试
- **Redis 写入成功，但事务内 Kafka 发送失败**：回滚当前事务；消息会再次消费，但由于 Redis 侧时间戳幂等控制，不会重复修改对象
- **事务提交前进程异常退出**：Kafka 不会暴露未提交事务中的下游消息，也不会提交对应 offset；消息恢复后会再次消费，但 Redis 会忽略旧事件
- **事务提交成功**：表示下游快照与对应 offset 已同时生效，不会出现“消息已发送但消费位点未推进”或“位点已推进但消息未发送”的中间状态

### 边界与约束

- 本方案保证的是**单对象维度的最终一致性**，不保证多个对象之间的事务一致性
- 本方案依赖同一对象的事件时间 `#time` 单调递增；如果上游产生相同对象的时间回退事件，则该事件会被视为旧数据忽略
- 由于 Redis 侧仅保留 `last_event_time`，如果上游不能保证同一对象 `#time` 严格递增，则必须额外引入唯一事件 ID 做去重
- 本方案默认同一对象消息路由到同一 Kafka 分区；如果分区策略被破坏，则对象更新顺序将无法保证
- Redis 与外部系统之间仍不存在分布式事务，因此本方案本质上仍是“Redis 最终一致 + Kafka 事务可见性一致”的组合方案

## 并发模型设计

应用采用“**固定线程池 + 每线程独立 Consumer + 全局共享 Producer**”的并发模型。

### 线程数量

- 启动时读取可用 CPU 核数，记为 `N`
- 默认开启 `2 x N` 个工作线程
- 这样可以在 Redis I/O、Kafka 网络发送和对象序列化之间获得更好的吞吐平衡

例如：当分配 CPU 为 4 核时，应用默认启动 8 个工作线程。

### Consumer 与 Producer 分配方式

- 每个工作线程持有各自独立的 Kafka Consumer，负责拉取并处理自己分配到的分区数据
- 所有工作线程共享同一个 Kafka Producer，用于发送下游对象快照
- 共享 Producer 统一开启事务能力，由提交线程按批次串行执行事务提交，避免多个线程并发提交事务造成冲突

这样的设计有两个好处：

- Consumer 彼此隔离，避免多线程共享 Consumer 带来的线程安全问题
- Producer 复用连接与缓冲区，减少网络连接数和内存占用

### 分区与顺序保证

- Kafka 的分区会被分配到不同 Consumer 线程上处理
- 同一分区在任意时刻只会被一个 Consumer 线程消费，因此同一对象仍然保持分区内顺序
- 由于上游已使用对象标识作为 key，同一对象的事件会落到同一分区，因此不会被多个线程并发更新

### 事务协调方式

- Worker 线程负责消费消息、调用 Redis Lua 脚本并生成最新快照
- Worker 不直接提交 Kafka 事务，而是将“待发送快照 + 分区 offset”提交到共享批次缓冲区
- 提交线程串行执行 `beginTransaction`、批量发送快照、提交消费 offset、`commitTransaction`
- 如果批次中的任一消息发送失败，则提交线程统一 `abortTransaction`，等待后续重新消费
- 发生 rebalance 时，相关 Worker 需要先停止拉取新消息，并等待当前批次完成提交或回滚后再释放分区

## 处理流程

单条消息的推荐处理顺序如下：

1. Consumer 拉取输入消息并完成反序列化
2. 根据 `app`、`type`、`#data_lifecycle`、`#identify` 计算 Redis key 与输出 topic
3. 调用 Redis Lua 脚本执行幂等判断和对象更新
4. 读取最新对象数据与元数据，组装输出快照
5. 组装快照时，将 `metadata.#created_time`、`metadata.#data_lifecycle`、`metadata.last_event_time` 分别映射为 `#created_time`、`#data_lifecycle`、`#event_time`，并补充发送时刻 `#updated_time`
6. 根据 `type` 将 `#identify` 映射为 `#user_id` 或 `#device_id`
7. Worker 将待发送快照和待提交 offset 放入共享事务批次
8. 提交线程在批次达到阈值或超时后开启事务并批量发送消息
9. 提交线程在同一事务中提交 offset
10. 事务成功提交后，批次对下游可见

## 异常消息与失败处理

- JSON 结构缺失关键字段，如 `app`、`type`、`data.#identify`、`data.#time` 时，记录错误日志后丢弃该数据
- `#operate` 不在支持范围内时，建议视为非法消息，不写 Redis、不提交正常下游快照
- 单字段类型错误不应导致整条事件失败；应记录告警并继续处理其他合法字段
- Redis 不可用、Lua 执行异常、Kafka 事务失败等基础设施异常应触发整批回滚，并依赖 Kafka 重试恢复

## 验证建议

上线前至少覆盖以下场景：

- 同一对象重复投递同一事件，确认 Redis 不会重复修改，`add` 不会重复累加
- 同一对象乱序到达旧事件，确认旧事件被忽略且下游输出仍为最新快照
- Redis 写入成功但 Kafka 事务回滚，确认重试后对象状态不重复变化
- 进程在事务提交前异常退出，重启后确认 offset 与下游可见消息保持一致
- Consumer rebalance 过程中无重复提交、无分区丢失、无对象顺序错乱

## 监控指标建议

- Kafka 消费 lag
- 单批事务提交耗时与失败次数
- Redis Lua 执行耗时与失败次数
- 幂等忽略事件数
- 非法消息数
- 下游快照发送 TPS 与失败次数

## 当前实现说明

- 当前仓库已经包含可运行的 Java 11 Maven 实现，覆盖了 `add`、`set`、`setOnce`、Redis 时间戳幂等、Kafka 事务发送、多 worker 启动、rebalance flush 和本地 smoke 验证脚本
- 当前 Redis 落库实现使用 `Lua + GET/SET + JSON 字符串文档` 来保证原子更新；数据结构和幂等语义与设计一致，但还没有切换到 `RedisJSON JSON.GET/JSON.SET`
- 本地验证环境使用 `Apache Kafka + Redis Stack`。Kafka 负责提供事务能力，Redis Stack 提供后续切换 RedisJSON 的基础镜像

## 本地运行

### 1. 运行单元测试

```bash
mvn -o test
```

### 2. 打包可执行 JAR

```bash
mvn -o -DskipTests package
```

产物路径：

```bash
target/funnydb-mutation-1.0-SNAPSHOT.jar
```

### 3. 准备本地配置

示例配置：

- [application-example.properties](/Users/huangsw/code/funny/funnydb/misc/funnydb-mutation/src/main/resources/application-example.properties)
- [application-local.properties](/Users/huangsw/code/funny/funnydb/misc/funnydb-mutation/src/main/resources/application-local.properties)

其中本地 smoke 默认使用：

- Kafka: `localhost:19092`
- Redis: `localhost:6379`
- `worker.thread-count=1`
- `tx.batch-size=1`

### 4. 启动本地依赖

```bash
./script/run-local-stack.sh
```

这会启动：

- Apache Kafka
- Redis Stack

对应编排文件：

- [docker-compose.yml](/Users/huangsw/code/funny/funnydb/misc/funnydb-mutation/docker-compose.yml)

### 5. 启动应用

```bash
./script/run-app-local.sh
```

等价命令：

```bash
java -jar target/funnydb-mutation-1.0-SNAPSHOT.jar src/main/resources/application-local.properties
```

## 本地 Smoke Test

一条命令执行本地端到端验证：

```bash
./script/run-smoke-test.sh
```

该脚本会自动完成：

1. 启动本地 Apache Kafka 和 Redis
2. 创建输入/输出 topic
3. 打包并后台启动应用
4. 发送一条 `UserMutation`
5. 校验 Redis 中对象文档
6. 校验输出 topic 中的最新快照

脚本文件：

- [run-smoke-test.sh](/Users/huangsw/code/funny/funnydb/misc/funnydb-mutation/script/run-smoke-test.sh)

## 已验证结果

本地 smoke 已实际跑通，验证到：

- 输入消息被成功写入 `funnydb-ingest-mutation-events`
- Redis 中生成对象文档 `demo:0:user:u-1`
- 输出 topic `demo-flink-users` 收到最新快照

一次实际输出示例如下。

Redis 文档：

```json
{"data":{"score":13,"nick":"alex-smoke"},"metadata":{"#data_lifecycle":"0","last_event_time":1003,"#created_time":1003}}
```

输出 topic 消息值：

```json
{"score":13,"nick":"alex-smoke","#event_time":1003,"#updated_time":1775558285001,"#created_time":1003,"#data_lifecycle":"0","#user_id":"u-1"}
```

## 已知注意事项

- 如果本机第一次运行 `docker compose up -d`，镜像拉取可能较慢
- 当前环境下如果应用进程运行在受限沙箱里，可能无法访问宿主机 `localhost:19092` 和 `localhost:6379`；直接在本机终端运行脚本即可避免这个问题
- `docker-compose` 目前使用 `apache/kafka:3.7.1` 的单节点 KRaft 配置，已开启事务相关参数，适合本地开发和 smoke test
