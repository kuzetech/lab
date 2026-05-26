# TaskManager 内存使用分解

## 各部分内存详解
- total memory
    - jvm memory
        - jvm-overhead.off-heap(JVM执行时自身所需要的内容，包括线程堆栈、IO、编译缓存等)
        - jvm-metaspace.off-heap(元数据存储)
    - flink memory
        - network.off-heap(网络数据交换所使用)
        - framework.heap(框架使用的堆内内存，即 TM 本身使用内存)
        - framework.off-heap(框架使用的堆外内存，即 TM 本身使用内存)
        - task.heap(用户代码使用的堆内内存)
        - task.off-heap(用户代码使用的堆外内存)
        - managed.off-heap(Flink管理的堆外内存，用于排序、哈希表、缓存中间结果及 RocksDB StateBackend 的本地内存)
