package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ConsumerUtil;
import com.kuzetech.bigdata.pulsar.util.PulsarUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class BatchIndexAckConsumer {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = PulsarUtil.getCommonPulsarClient();
                Consumer<byte[]> consumer = ConsumerUtil.getBatchIndexAcknowledgmentConsumer(client, "sink-topic")
        ) {
            for (int i = 0; i < 10; i++) {
                // 当生产者发送一条批消息，该消息会作为多条消息的集合被独立存储，在被消费时又作为一个整体被发送到消费端
                // 客户端会将消息拆解，调用 receive 时每次拿取一条
                Message<byte[]> msg = consumer.receive();
                try {
                    System.out.println("Message received: " + new String(msg.getData()));
                    // 对批消息中的前几条消息进行确认时，不会导致该批次的所有消息都被确认，但下次重启消费时会重复消费数据
                    consumer.acknowledge(msg);
                } catch (Exception e) {
                    // 对批消息中的任意一条消息进行否认确认时，都会导致该批次的所有消息被重新发送
                    consumer.negativeAcknowledge(msg);
                }
            }

            /*
             * 为了避免将确认的消息批量重新发送给消费者，Pulsar从2.6.0版本开始引入了批量索引确认机制
             * 如果启用，则服务端会维护批量索引确认状态并跟踪每个批量索引的确认状态，以避免将已确认的消息分发给消费者。
             * 当批消息的所有索引都得到确认时，批消息将被删除。
             * 默认情况下，服务端和客户端都默认禁用了批量索引确认机制，需要保证都开启
             * 因为要维护更多的批量索引信息，所以启用批量索引确认后会导致更多的内存开销
             * */
        }
    }
}
