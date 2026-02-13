package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.ClientUtil;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.transaction.Transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class TransactionProducer {
    public static void main(String[] args) {
        try (
                PulsarClient client = ClientUtil.createTransactionLocalClient();
                Producer<String> producer = client.newProducer(Schema.STRING)
                        .producerName("lab-producer-transaction")
                        .topic("persistent://public/default/source")
                        .create()
        ) {
            // 每秒发送一条数据
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int messageCount = 0;

            while (true) {
                // 开启事务
                Transaction transaction = client.newTransaction()
                        .withTransactionTimeout(30, TimeUnit.SECONDS)
                        .build()
                        .get();

                try {
                    // 在事务中发送消息
                    String message = String.format("Message %d - %s", messageCount, LocalDateTime.now().format(formatter));
                    MessageId messageId = producer.newMessage(transaction)
                            .value(message)
                            .send();

                    // 提交事务
                    transaction.commit().get();

                    System.out.println("Successfully sent message: " + message + " with MessageId: " + messageId);
                    messageCount++;
                } catch (Exception e) {
                    // 事务中发生异常时回滚
                    try {
                        transaction.abort().get();
                    } catch (Exception abortException) {
                        System.err.println("Failed to abort transaction: " + abortException.getMessage());
                    }
                    System.err.println("Failed to send message, transaction aborted: " + e.getMessage());
                }

                // 每秒发送一次
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("Thread interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in TransactionProducer: " + e.getMessage());
        }
    }
}
