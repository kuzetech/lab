package com.kuze.bigdata;

public class Controller {

    /*
    控制器组件（Controller）
        是 Apache Kafka 的核心组件。它的主要作用是在 Apache ZooKeeper 的帮助下管理和协调整个 Kafka 集群。
        集群中任意一台 Broker 都能充当控制器的角色，但是，在运行过程中，只能有一个 Broker 成为控制器，行使其管理和协调的职责。
        换句话说，每个正常运转的 Kafka 集群，在任意时刻都有且只有一个控制器。
        官网上有个名为 activeController 的 JMX 指标，可以帮助我们实时监控控制器的存活状态。
        这个 JMX 指标非常关键，你在实际运维操作过程中，一定要实时查看这个指标的值。

    控制器的选举机制
        Broker 在启动时，会尝试去 ZooKeeper 中创建 /controller 节点。
        Kafka 当前选举控制器的规则是：第一个成功创建 /controller 节点的 Broker 会被指定为控制器

    控制器的作用
        主题管理，就是指控制器帮助我们完成对 Kafka 主题的创建、删除以及分区增加的操作。换句话说，当我们执行 kafka-topics 脚本时，大部分的后台工作都是控制器来完成的
        分区重分配，主要是指运行 kafka-reassign-partitions 脚本提供的对已有主题分区进行细粒度的分配功能
        Preferred 领导者选举，主要是 Kafka 为了避免部分 Broker 负载过重而提供的一种换 Leader 的方案
        集群成员管理，包含新增 Broker、Broker 主动关闭、Broker 宕机
            这种自动检测是依赖于前面提到的 Watch 功能和 ZooKeeper 临时节点组合实现的。比如，控制器组件会利用 Watch 机制检查 ZooKeeper 的 /brokers/ids 节点下的子节点数量变更。
            目前，当有新 Broker 启动后，它会在 /brokers 下创建专属的 znode 节点。一旦创建完毕，ZooKeeper 会通过 Watch 机制将消息通知推送给控制器，
            这样，控制器就能自动地感知到这个变化，进而开启后续的新增 Broker 作业。
            侦测 Broker 存活性则是依赖于刚刚提到的另一个机制：临时节点。每个 Broker 启动后，会在 /brokers/ids 下创建一个临时 znode。
            当 Broker 宕机或主动关闭后，该 Broker 与 ZooKeeper 的会话结束，这个 znode 会被自动删除。
            同理，ZooKeeper 的 Watch 机制将这一变更推送给控制器，这样控制器就能知道有 Broker 关闭或宕机了，从而进行“善后”。
        数据服务，控制器的最后一大类工作，就是向其他 Broker 提供数据服务。控制器上保存了最全的集群元数据信息，其他所有 Broker 会定期接收控制器发来的元数据更新请求，从而更新其内存中的缓存数据
            比较重要的数据有：
                所有主题信息。包括具体的分区信息，比如领导者副本是谁，ISR 集合中有哪些副本等。
                所有 Broker 信息。包括当前都有哪些运行中的 Broker，哪些正在关闭中的 Broker 等。
                所有涉及运维任务的分区。包括当前正在进行 Preferred 领导者选举以及分区重分配的分区列表
            值得注意的是，这些数据其实在 ZooKeeper 中也保存了一份。每当控制器初始化时，它都会从 ZooKeeper 上读取对应的元数据并填充到自己的缓存中。
            有了这些数据，控制器就能对外提供数据服务了。这里的对外主要是指对其他 Broker 而言，控制器通过向这些 Broker 发送请求的方式将这些数据同步到其他 Broker 上

    控制器故障转移
    控制器内部设计原理


    */

}
