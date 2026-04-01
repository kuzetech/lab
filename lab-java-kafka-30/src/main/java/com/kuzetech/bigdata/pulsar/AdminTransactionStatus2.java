package com.kuzetech.bigdata.pulsar;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeTransactionsResult;
import org.apache.kafka.clients.admin.TransactionDescription;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class AdminTransactionStatus2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final String transactionalId = "my-transactional-id-002";

        Properties adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        try (Admin admin = Admin.create(adminProps)) {
            DescribeTransactionsResult result = admin.describeTransactions(Collections.singletonList(transactionalId));
            TransactionDescription description = result.description(transactionalId).get();
            System.out.println("---------------------------------");
            System.out.println("事务 ID: " + transactionalId);
            System.out.println("当前状态: " + description.state());
            System.out.println("关联的 Producer ID: " + description.producerId());
            System.out.println("事务超时时间: " + description.transactionTimeoutMs() + "ms");
        }
    }
}
