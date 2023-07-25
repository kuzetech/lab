package com.kuzetech.bigdata.study;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
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
        * 一旦设置了 enable.auto.commit 为 true，
        * 默认 auto.commit.interval.ms 的值，一个 Consumer 将会提交它的 Offset 给 Kafka，
        * 或者每一次数据从指定的 Topic 取回时，将会提交最后一次的 Offset
        * 因此它能保证不出现消费丢失的情况，但它可能会出现重复消费
        * 在默认情况下，Consumer 每 5 秒自动提交一次位移。现在，我们假设提交位移之后的 3 秒发生了 Rebalance 操作。
        * 在 Rebalance 之后，所有 Consumer 从上一次提交的位移处继续消费，但该位移已经是 3 秒前的位移数据了，
        * 故在 Rebalance 发生前 3 秒消费的所有数据都要重新再消费一次
        *
        * 自动提交时间设置并不是非常严格,可能会大于设置的时间间隔;
        * 自动提交并不是严格地每间隔一段时间提交一次偏移量，而是每次在调用 KafkaConsumer.poll()时判断当前时间距离上次提交时间是否超过了配置了提交间隔，
        * 如果超过了就进行提交，所以实际上的提交时间会超过配置的提交间隔
        *
        * */
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        /*
        * 当 Consumer Group 完成 Rebalance 之后，每个 Consumer 实例都会定期地向 Coordinator 发送心跳请求，表明它还存活着。
        * 如果某个 Consumer 实例超过配置时间发送心跳请求，Coordinator 就会认为该 Consumer 已经“死”了，从而将其从 Group 中移除，然后开启新一轮 Rebalance
        *
        * */
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        /*
        * 发送心跳请求频率的参数
        * 这个值设置得越小，Consumer 实例发送心跳请求的频率就越高。
        * 频繁地发送心跳请求会额外消耗带宽资源，但好处是能够更加快速地知晓当前是否开启 Rebalance，
        * 因为，目前 Coordinator 通知各个 Consumer 实例开启 Rebalance 的方法，就是将 REBALANCE_NEEDED 标志封装进心跳请求的响应体中
        *
        * */
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        /*
        * 最佳实践
        * 设置 session.timeout.ms = 6s
        * 设置 heartbeat.interval.ms = 2s
        * 要保证 Consumer 实例在被判定为“dead”之前，能够发送至少 3 轮的心跳请求，即 session.timeout.ms >= 3 * heartbeat.interval.ms。
        * 将 session.timeout.ms 设置成 6s 主要是为了让 Coordinator 能够更快地定位已经挂掉的 Consumer。
        * 毕竟，我们还是希望能尽快揪出那些“尸位素餐”的 Consumer，早日把它们踢出 Group。
        * 希望这份配置能够较好地帮助你规避第一类“不必要”的 Rebalance。
        * */


        /*
        * 用于控制 Consumer 实际消费能力对 Rebalance 的影响
        * 它限定了 Consumer 端应用程序两次调用 poll 方法的最大时间间隔
        * 你的 Consumer 程序如果在 5 分钟之内无法消费完 poll 方法返回的消息
        * 那么 Consumer 会主动发起“离开组”的请求，Coordinator 也会开启新一轮 Rebalance
        *
        * */
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1000 * 60 * 5);


        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);


        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        int count = 0;

        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record: records) {
                    // 处理消息
                    // process(record);

                    offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
                    if(count % 100 == 0){
                        consumer.commitAsync(offsets, null); // 回调处理逻辑是null
                    }
                    count++;
                }

                if(count % 100 > 0){
                    consumer.commitAsync(offsets, null); // 回调处理逻辑是null
                }
            } catch(Exception e) {
                // 处理异常

            } finally {
                try {
                    consumer.commitSync(); // 最后一次提交使用同步阻塞式提交
                } finally {
                    consumer.close();
                }
            }
        }


    }


}
