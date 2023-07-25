package com.kuzetech.bigdata.study;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class MyPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        // cluster contains The current cluster metadata
        // cluster则是集群信息（比如当前 Kafka 集群共有多少主题、多少 Broker 等）
        return 0;
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // 根据一开始传入 producer 的配置信息来配置当前的分区器
    }
}
