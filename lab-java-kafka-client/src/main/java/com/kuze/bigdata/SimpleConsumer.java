package com.kuze.bigdata;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Hello world!
 *
 */
public class SimpleConsumer {

    public static final Logger logger = LoggerFactory.getLogger(SimpleConsumer.class);

    public static void main( String[] args ){

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        /*
        * 如果生产者和 broker 配置了不同的压缩算法会导致 broker 需要解压后重新按照算法压缩
        * broker 端配置参数也是 compression.type ，并且默认值是 producer，这表示 Broker 端会采用 Producer 端使用的压缩算法
        * */
        // 开启 GZIP 压缩
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");



    }


}
