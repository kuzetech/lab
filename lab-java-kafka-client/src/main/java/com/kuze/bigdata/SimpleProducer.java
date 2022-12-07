package com.kuze.bigdata;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Hello world!
 *
 */
public class SimpleProducer {

    public static final Logger logger = LoggerFactory.getLogger(SimpleProducer.class);

    public static void main( String[] args ){

        Properties props = new Properties();

        // Producer 启动时会发起与这些 Broker 的连接。因此，如果你为这个参数指定了 1000 个 Broker 连接信息，
        // 那么很遗憾，你的 Producer 启动时会首先创建与这 1000 个 Broker 的 TCP 连接
        // 在实际使用过程中，我并不建议把集群中所有的 Broker 信息都配置到 bootstrap.servers 中，
        // 通常你指定 3～4 台就足以了。因为 Producer 一旦连接到集群中的任一台 Broker，就能拿到整个集群的 Broker 信息
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // broker 端可以指定 min.insync.replicas 配置消息至少要被写入到多少个副本才算是“已提交”
        // 并不一定总是所有副本都写入才算是“已提交”
        // replication.factor = min.insync.replicas + 1，如果 replication.factor = min.insync.replicas 只要有一个副本挂了就无法服务了
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        props.put(ProducerConfig.RETRIES_CONFIG, 3);


        /*
        * Kafka 拦截器分为生产者拦截器和消费者拦截器
        * 需要继承 ProducerInterceptor 和 ConsumerInterceptor
        * */
        // 添加拦截器
        List interceptors = new ArrayList<>();
        interceptors.add("com.kuze.bigdata.AddTimestampInterceptor");
        //interceptors.add("com.yourcompany.kafkaproject.interceptors.UpdateCounterInterceptor");
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

        /*
        * 如果生产者和 broker 配置了不同的压缩算法会导致 broker 需要解压后重新按照算法压缩
        * broker 端配置参数也是 compression.type ，并且默认值是 producer，这表示 Broker 端会采用 Producer 端使用的压缩算法
        * Kafka 会将启用了哪种压缩算法封装进消息集合中，这样当 Consumer 读取到消息集合时，它自然就知道了这些消息使用的是哪种压缩算法
        * 整个流程就是 Producer 端压缩、Broker 端保持、Consumer 端解压缩
        *
        * 吞吐量方面：LZ4  > Snappy > zstd 和 GZIP
        * 压缩比方面，zstd > LZ4    > GZIP > Snappy
        * 在 CPU 使用率方面，各个算法表现得差不多，
        * 只是在压缩时 Snappy 算法使用的 CPU 较多一些，而在解压缩时 GZIP 算法则可能使用更多的 CPU
        * */
        // 开启 lz4 压缩
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

        /*
         * 默认情况下 如果指定了 Key，那么默认实现按 key hash 分配；如果没有指定 Key，则使用轮询策略
         * 当然也可以在生成 ProducerRecord 直接指定要发送到哪个分区
         * */
        // 设置自定义消息分区器
        // properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,"com.kuze.bigdata.MyPartitioner");

        KafkaProducer producer = new KafkaProducer<Integer, String>(props);


        try {
            ProducerRecord record = new ProducerRecord(
                    "myTopic",
                    "test"
            );
            // 该方法会阻塞直到结果返回或者超时
            producer.send(record).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("发送过程中被打断", e);
        } catch (ExecutionException e) {
            logger.error("发送过程中出现异常", e);
        } catch (TimeoutException e) {
            logger.error("发送超时", e);
        }finally {
            producer.close();
        }

    }


}
