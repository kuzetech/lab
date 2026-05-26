# rocksdb 内存使用说明

作业默认开启 state.backend.rocksdb.memory.managed=true，系统会确保 rocksdb 使用的内存正好与 Flink 的托管内存预算相同
Managed Memory 会根据 TaskManager 的 Slot 槽位数 进行平均切分。
每个 Slot 只能获取自己那份额度的 Managed Memory，这确保了不同 Subtask 之间的资源隔离。

Flink 允许一个 Slot 运行多个不同的算子子任务。所以 chain 合并后的每个 subtask 拥有自己的 rocksdb 实例。
如果一个 Slot 里跑了 Window 算子和 Join 算子，那么这个 Slot 里就会同时存在两个物理上隔离的 RocksDB 实例（本地目录也是分开的）。

每个 slot 会开启独立的 shared read cache + write buffer manager
其中 shared cache 负责 data block cache、index、bloom filters
write buffer manager 主要负责 MemTables
slot 上所有的 RocksDB 实例读缓存共用 shared cache
但写入则在 write buffer manager 划分独立的内存空间。

对于 Slot 分配的内存池有如下分配规则：
state.backend.rocksdb.memory.write-buffer-ratio，默认值 0.5，即 50% 的给定内存会分配给写缓冲区使用。
state.backend.rocksdb.memory.high-prio-pool-ratio，默认值 0.1，即 10% 的 block cache 内存会优先分配给索引及过滤器。

实际计算公式如下：
cache_capacity = (3 - write_buffer_ratio) * total_memory / 3

为此，如果 TM 上的 Managed Memory = 5.96 GB，并且 Slot = 1
则，每个 Slot 分配的内存池为 5.96 GB
cache_capacity = (3 - 0.5) * 5.96 / 3 = 4.97 GB
write_buffer_manager_capacity = 5.96 - 4.97 = 0.99 GB

high-prio-pool-ratio=0.1 是从 block-cache-capacity 里面切
high-priority-pool-capacity = 4.97 * 0.1 = 0.5 GB
high-priority-pool-capacity主要留给：
- index blocks
- bloom filter blocks
- compression dictionary blocks

rocksdb 中 index/filter 可以存放在 block-cache 中，也可以存放在 block-cache 外。可以通过 cache_index_and_filter_blocks 参数设置。
但是对 partitioned index/filter 是个特例，官方说明这些 partition 通常会进 block cache 中。
所以指标 estimate_table_readers_mem 看的是 table reader 自身/附属结构在 block cache 之外的那部分内存，不是 block cache 里的 index/filter block。

可以在作业中开启以下关键指标：
state.backend.rocksdb.metrics.block-cache-hit: 'true'
state.backend.rocksdb.metrics.block-cache-miss: 'true'
state.backend.rocksdb.metrics.block-cache-capacity: 'true'
state.backend.rocksdb.metrics.block-cache-usage: 'true'
state.backend.rocksdb.metrics.cur-size-all-mem-tables: 'true'
state.backend.rocksdb.metrics.estimate-table-readers-mem: 'true'
state.backend.rocksdb.metrics.estimate-live-data-size: 'true'
state.backend.rocksdb.metrics.num-running-compactions: 'true'
state.backend.rocksdb.metrics.num-running-flushes: 'true'

并在 grafana 中有 如下指标：
----------------------------------------------------------------------------------------------------------------------
sum by (operator_name) (
  rate(flink_taskmanager_job_task_operator_rocksdb_block_cache_hit{tm_id="funnydb_mutation_event_process_1b1b9dcb_app_taskmanager_1_4"}[5m])
)
/
sum by (operator_name) (
  rate(flink_taskmanager_job_task_operator_rocksdb_block_cache_hit{tm_id="funnydb_mutation_event_process_1b1b9dcb_app_taskmanager_1_4"}[5m]) +
  rate(flink_taskmanager_job_task_operator_rocksdb_block_cache_miss{tm_id="funnydb_mutation_event_process_1b1b9dcb_app_taskmanager_1_4"}[5m])
)
----------------------------------------------------------------------------------------------------------------------
max({
  __name__=~"flink_taskmanager_job_task_operator_.*_rocksdb_block_cache_capacity",
  tm_id="funnydb_mutation_event_process_1b1b9dcb_app_taskmanager_1_4"
}) by (tm_id) /1024/1024/1024
----------------------------------------------------------------------------------------------------------------------
max({
  __name__=~"flink_taskmanager_job_task_operator_.*_rocksdb_block_cache_usage",
  tm_id="funnydb_mutation_event_process_1b1b9dcb_app_taskmanager_1_4"
}) by (tm_id) /1024/1024/1024
----------------------------------------------------------------------------------------------------------------------
sum({
  __name__=~"flink_taskmanager_job_task_operator_.*_rocksdb_estimate_table_readers_mem",
  tm_id="funnydb_mutation_event_process_1b1b9dcb_app_taskmanager_1_4"
}) by (tm_id) /1024
----------------------------------------------------------------------------------------------------------------------
sum({
  __name__=~"flink_taskmanager_job_task_operator_.*_rocksdb_cur_size_all_mem_tables",
  tm_id="funnydb_mutation_event_process_1b1b9dcb_app_taskmanager_1_4",
  operator_name="deduplicate_mutation_event"
}) by (operator_name) /1024/1024
----------------------------------------------------------------------------------------------------------------------
sum({
  __name__=~"flink_taskmanager_job_task_operator_.*_rocksdb_estimate_live_data_size",
  tm_id="funnydb_mutation_event_process_1b1b9dcb_app_taskmanager_1_4"
}) by (operator_name) /1024/1024/1024
----------------------------------------------------------------------------------------------------------------------
sum({
  __name__=~"flink_taskmanager_job_task_operator_.*_rocksdb_num_running_compactions",
  tm_id="funnydb_mutation_event_process_1b1b9dcb_app_taskmanager_1_4"
}) by (operator_name)
----------------------------------------------------------------------------------------------------------------------
sum({
  __name__=~"flink_taskmanager_job_task_operator_.*_rocksdb_num_running_flushes",
  tm_id="funnydb_mutation_event_process_1b1b9dcb_app_taskmanager_1_4"
}) by (operator_name)
----------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------