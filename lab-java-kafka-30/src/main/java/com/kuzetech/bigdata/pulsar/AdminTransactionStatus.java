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

        // 3. 初始化事务
        producer.initTransactions();
        producer.beginTransaction();
        producer.send(new ProducerRecord<>("test", "test"));
        producer.flush();

        Properties adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        Admin admin = Admin.create(adminProps);

        DescribeTransactionsResult result = admin.describeTransactions(Collections.singletonList(transactionalId));
        TransactionDescription description = result.description(transactionalId).get();
        System.out.println("事务 ID: " + transactionalId);
        System.out.println("当前状态: " + description.state());
        System.out.println("关联的 Producer ID: " + description.producerId());
        System.out.println("事务超时时间: " + description.transactionTimeoutMs() + "ms");

        producer.abortTransaction();

        Thread.sleep(3000);

        DescribeTransactionsResult result2 = admin.describeTransactions(Collections.singletonList(transactionalId));
        TransactionDescription description2 = result2.description(transactionalId).get();
        System.out.println("事务 ID: " + transactionalId);
        System.out.println("当前状态: " + description2.state());
        System.out.println("关联的 Producer ID: " + description2.producerId());
        System.out.println("事务超时时间: " + description2.transactionTimeoutMs() + "ms");

        admin.close();
        producer.close();
    }
}
