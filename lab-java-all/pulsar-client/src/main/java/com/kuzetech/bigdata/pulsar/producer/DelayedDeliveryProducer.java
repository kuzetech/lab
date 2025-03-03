package com.kuzetech.bigdata.pulsar.producer;

import com.kuzetech.bigdata.pulsar.util.ProducerUtil;
import com.kuzetech.bigdata.pulsar.util.PulsarUtil;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DelayedDeliveryProducer {
    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Producer<byte[]> producer = ProducerUtil.getSimpleProducer(client, "source-topic");
        ) {
            /**
             * 对于消息中间件来说，除了正常生产、发送消息的需求外，有时可能并不希望这条消息马上被消费，而是希望推迟到某个时间点后再投递到消费者中进行消费。
             * 比如，在很多在线业务系统中，可能由于业务逻辑处理出现异常，希望此条消息在一段时间后被重新消费；
             * 也可能因为特殊业务的逻辑，需要在固定时间窗口后进行再消费相关的消息；
             * 例如在电商系统中，订单创建后会有一个等待用户支付的时间窗口，若在该时间窗口内未支付订单则需要在窗口结束后清理掉原有的订单
             */


            // 固定延迟发送
            producer.newMessage()
                    .deliverAfter(3, TimeUnit.MINUTES)
                    .value("延迟消息".getBytes(StandardCharsets.UTF_8))
                    .send();

            // 固定时间点发送
            Date date = new Date(2025, 2, 2, 0, 0, 0);
            producer.newMessage()
                    .deliverAt(date.getTime())
                    .value("定时消息".getBytes(StandardCharsets.UTF_8))
                    .send();

            /**
             * 在使用消息延迟传递时，需要保证服务端开启该功能。
             * 在Broker配置文件中通过参数delayedDeliveryEnabled控制该功能，默认情况下服务端已经启用了消息延迟传递功能。
             * 消息延迟传递功能仅适用于共享订阅类型。在独占订阅和故障转移订阅类型中，为了确保消费的先入先出特性，该功能不会生效。
             * 开启消息延迟传递功能后，消息从生产者到服务端的发送流程与普通消息无异，只是每条消息中会带有延迟消息参数。
             * 当服务端开启消息延迟传递功能时，共享模式下的订阅中会启动数据分发器中的消息延迟追踪器(DelayedDeliveryTracker)，此时该订阅即可支持消息延迟传递功能。
             * 支持多个消费者发送消息的发送器在发送消息时，会检查每条消息带有的延迟消息参数。
             * 当消费者消费一条消息时，如果该消息被设置为延迟传递，则该消息会被添加到消息延迟追踪器队列中。
             * 该消息到期后会被重新发送。消息延迟的实现其实是通过时间轮(Time Wheel approach)算法实现的。
             * 时间轮由服务端配置的最小触发时间索引。例如，如果我们将最小触发时间配置为1秒，那么我们要为需要延迟5分钟的任务维护300个触发任务。
             * 时间轮算法会从队列中挑选出到期的消息，并将它们分派给真正的消费者。
             * 因此，最小触发时间代表着消息延迟的精度，在消息延迟传递功能生效时，服务端可以通过参数delayedDeliveryTickTimeMillis控制最小触发时间，默认情况下为1s。
             * 消息延迟传递的实现原理如图6-8所示。一条延迟消息被发送后，会正常存储在BookKeeper中，在使用独占订阅模式或故障转移订阅模式时，延迟消息会正常被消费。
             * 而在共享订阅模式中，会通过消息延迟追踪器在服务端内存中维护消息延迟时间索引，一旦消息过了特定的延迟时间，它就会被传递给消费者
             */
        }
    }
}
