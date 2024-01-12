package com.kuzetech.bigdata.kafka.admin;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class GetTopicPartitionNumber {

    public static void main(String[] args) throws InterruptedException {
        String topic = "test";
        String server = "localhost:9092";

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        try (AdminClient adminClient = AdminClient.create(props)) {
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singletonList(topic));
            Map<String, TopicDescription> topicsDescMap = describeTopicsResult.allTopicNames().get();
            TopicDescription topicDescription = topicsDescMap.get(topic);
            if (topicDescription == null || topicDescription.partitions().isEmpty()) {
                System.err.println("Failed to retrieve partition information for the specified topic.");
                return;
            }
            System.out.println("Number of Partitions: " + topicDescription.partitions().size());
        } catch (ExecutionException e) {
            if (e.getCause() instanceof UnknownTopicOrPartitionException) {
                System.err.println("topic not exist");
            } else {
                System.err.println("other error");
            }
        }
    }
}
