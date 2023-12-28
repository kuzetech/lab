package com.kuzetech.bigdata.kafka;

public class HighWatermarkAndEpoch {
    /*
    * 因为副本同步机制都是异步
    * 因此使用高水位机制，来判断哪些消息是所有副本都同步到位了，可以对外消费了
    * 但是单独使用高水位机制有丢失数据的风险，所以需要 epoch 配合
    * */
}
