package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import com.kuzetech.bigdata.pulsar.util.ReaderUtil;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Reader;

/**
 * Pulsar 的 Reader 接口可以使我们通过应用程序手动管理访问游标。
 * 当使用Reader接口访问主题时，需要指定Reader在连接到主题时开始读取的消息的位置，例如最早和最后可以访问到的有效消息位置，你也可以通过构建MessageId来指定任意有效位置进行消息访问。
 * 在Pulsar作为流处理系统对外提供“精确一次”处理语义等用例时，Reader接口非常有用。对于此类用例，流处理系统必须能够将主题“倒带”到特定消息所在位置并在那里开始阅读
 */
public class ReaderSimpleApp {

    public static void main(String[] args) throws PulsarClientException {
        try (
                PulsarClient client = ClientUtil.createDefaultLocalClient()
        ) {
            // 内部实现中，读取器也是通过消费者功能封装的，内部使用一个随机命名的订阅名称来对主题进行独占、非持久性订阅，以到达手动定位消息的目的。
            Reader<byte[]> reader = ReaderUtil.getCommonReader(client, "sink-topic");
            while (true) {
                Message<byte[]> msg = reader.readNext();
                System.out.println("Message received: " + new String(msg.getData()));
            }
        }
    }
}