package com.kuzetech.bigdata.pulsar.util;

import com.kuzetech.bigdata.pulsar.constant.BaseConstant;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SizeUnit;

import java.util.concurrent.TimeUnit;

public class ClientUtil {

    public static PulsarClient createDefaultLocalClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl(BaseConstant.DEFAULT_LOCAL_SERVICE_URL)
                // 处理服务端连接的线程个数
                .ioThreads(Runtime.getRuntime().availableProcessors())
                // 主要用于消费者，处理消息监听和拉取的线程个数
                .listenerThreads(Runtime.getRuntime().availableProcessors())
                // 在客户端处理Broker请求时，每个Broker对应建立多少个连接
                .connectionsPerBroker(1)
                // 通过日志来打印客户端统计信息的时间间隔
                .statsInterval(60, TimeUnit.SECONDS)
                .memoryLimit(64, SizeUnit.MEGA_BYTES)
                .listenerName("external")
                .build();
    }
}
