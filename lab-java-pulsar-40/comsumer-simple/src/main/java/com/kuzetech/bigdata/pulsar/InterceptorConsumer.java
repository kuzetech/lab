package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ConsumerUtil;
import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

// 该功能可以让用户在消费者中对消息生命周期中的各个节点进行功能增强。
// 例如，对接收后的消息进行转化过滤处理，在消息被确认时调用业务逻辑，在消息确认超时时进行信息统计
// 案例需要进行补充
public class InterceptorConsumer {
    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient();
                Consumer<byte[]> consumer = ConsumerUtil.getCommonConsumer(client, "sink-topic")
        ) {

        }
    }
}
