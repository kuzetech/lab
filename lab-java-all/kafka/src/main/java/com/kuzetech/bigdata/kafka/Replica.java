package com.kuzetech.bigdata.kafka;

public class Replica {

    /*
    * replica.lag.time.max.ms
    * 这个参数的含义是 Follower 副本能够落后 Leader 副本的最长时间间隔，当前默认值是 10 秒。
    * 这就是说，只要一个 Follower 副本落后 Leader 副本的时间不连续超过 10 秒，
    * 那么 Kafka 就认为该 Follower 副本与 Leader 是同步的，即使此时 Follower 副本中保存的消息明显少于 Leader 副本中的消息
    *
    * Follower 副本唯一的工作就是不断地从 Leader 副本拉取消息，然后写入到自己的提交日志中。
    * 如果这个同步过程的速度持续慢于 Leader 副本的消息写入速度，
    * 那么在 replica.lag.time.max.ms 时间后，此 Follower 副本就会被认为是与 Leader 副本不同步的，因此不能再放入 ISR 中。
    * 此时，Kafka 会自动收缩 ISR 集合，将该副本“踢出”ISR
    *
    *
    *
    * */
}
