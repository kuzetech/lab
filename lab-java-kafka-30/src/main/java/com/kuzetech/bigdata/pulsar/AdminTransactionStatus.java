package com.kuzetech.bigdata.pulsar;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeTransactionsResult;
import org.apache.kafka.clients.admin.TransactionDescription;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class AdminTransactionStatus {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final String transactionalId = "my-transactional-id-002";

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        
        // 向 Kafka Coordinator 发送请求,如果有未完成的事务则取消
        // 因此无论原来的事务状态是什么，都会变成 Empty
        producer.initTransactions();
        // 仅仅是在生产者本地将状态标记为 IN_TRANSACTION，不会与 Broker 通信
        producer.beginTransaction();

        // 当生产者第一次向某个 TopicPartition 发送数据时，它会先发送一个 AddPartitionsToTxnRequest 给 Coordinator。
        // 只有当 Coordinator 收到这个请求，意识到“哦，你要开始往分区写东西了”，它才会将状态从 Empty 改为 Ongoing
        producer.send(new ProducerRecord<>("test", "test"));

        Properties adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        Admin admin = Admin.create(adminProps);

        DescribeTransactionsResult result = admin.describeTransactions(Collections.singletonList(transactionalId));
        TransactionDescription description = result.description(transactionalId).get();
        System.out.println("---------------------------------");
        System.out.println("事务 ID: " + transactionalId);
        System.out.println("当前状态: " + description.state());
        System.out.println("关联的 Producer ID: " + description.producerId());
        System.out.println("事务超时时间: " + description.transactionTimeoutMs() + "ms");

        // 到此事务状态都是 CompleteCommit
        producer.commitTransaction();

        Thread.sleep(3000);
        DescribeTransactionsResult result2 = admin.describeTransactions(Collections.singletonList(transactionalId));
        TransactionDescription description2 = result2.description(transactionalId).get();
        System.out.println("---------------------------------");
        System.out.println("事务 ID: " + transactionalId);
        System.out.println("当前状态: " + description2.state());
        System.out.println("关联的 Producer ID: " + description2.producerId());
        System.out.println("事务超时时间: " + description2.transactionTimeoutMs() + "ms");

        admin.close();
        producer.close();
    }
}
