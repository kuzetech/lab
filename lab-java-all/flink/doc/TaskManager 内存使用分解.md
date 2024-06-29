# TaskManager 内存使用分解

## 官网地址
 
[粗略调整 TM 内存](https://nightlies.apache.org/flink/flink-docs-release-1.19/zh/docs/deployment/memory/mem_setup/)  

<img src="https://nightlies.apache.org/flink/flink-docs-release-1.19/fig/process_mem_model.svg" alt="粗略内存分布" width="300" height="200" />  

[精细调整 TM 内存](https://nightlies.apache.org/flink/flink-docs-release-1.19/zh/docs/deployment/memory/mem_setup_tm/)  

<img src="https://nightlies.apache.org/flink/flink-docs-release-1.19/fig/detailed-mem-model.svg" alt="精细内存分布" width="300" height="400" />  


## 各部分内存详解

- total process Memory
  - jvm process memory
    - jvm-overhead.off-heap
      - jvm 开销内存，一般用于临时的垃圾回收或栈空间的分配
      - 默认最小值为 192mb，默认最大值为 1gb，默认百分比为 0.1
      - 新项目可以调整 fraction 保持默认值，如果提示不足再逐渐增大为 256mb、512mb、768mb、1gb
    - jvm-metaspace.off-heap
      - jvm 元数据存储
      - 默认为固定值 256mb
      - 新项目可以保持默认值，如果提示不足可以先分配大额一些，任务启动后观察 webUI 实际使用情况再进行精确调整
  - total flink memory
    - network.off-heap
      - 用于任务之间数据传输的直接内存
      - 默认最小值为 64mb，默认最大值为无上限，默认百分比为 0.1
      - 从生产环境观测该内存使用量一般不超过 200mb，新项目可以调整 fraction 保持 200mb，任务启动后观察 webUI 实际使用情况再进行精确调整
    - framework.heap
      - flink 框架使用的堆内内存
      - 默认为固定值 128mb
      - 新项目一般不需要调整，如果提示不足每次增加 128mb 直至可以启动
    - framework.off-heap
      - flink 框架使用的堆外内存
      - 默认为固定值 128mb
      - 新项目默认值即可，遇到需要调整的情况是该参数随着从 kafka 消费的数据量增加也需要对应的增加，可以尝试调整 512mb、1gb、2gb 直至系统长期稳定运行
    - task.heap
      - 应用算子和用户代码使用的堆内内存，该内存会被每个 slot 均分
      - 扣除其余部分后剩下
      - 新项目一般保证每个 slot 分配 64mb，如果提示不足可以先分配大额一些，任务启动后观察 webUI 实际使用情况再进行精确调整，一般预留三分之一内存
    - task.off-heap
      - 应用算子和用户代码使用的堆外内存
      - 默认为 0mb
      - 一般来说根本不会用到这一块，所以无需调整
    - managed.off-heap
      - 用于排序、哈希表、中间结果缓存、rocksdb 状态后端使用的堆外内存
      - 默认百分比为 0.4
      - 不同情况的分析
        - 作业中单独或者混合使用排序、哈希表、中间结果缓存
          - 没开发过不清楚
        - 作业中仅使用 rocksdb 状态后端
          - 一个 taskmanager 只会启动一个 RocksDB，所有的 slot 将共用该实例
          - 管理内存也会平均划分给每个 slot，如果该 slot 被分配了状态任务，这部分内存就会划分给 RocksDB
          - RocksDB State Backend 的性能在很大程度上取决于它可用的内存量
        - 作业中混合使用排序、哈希表、中间结果缓存、rocksdb 状态后端
          - 没开发过不清楚
          - 很好奇他们之间的内存分配比例
