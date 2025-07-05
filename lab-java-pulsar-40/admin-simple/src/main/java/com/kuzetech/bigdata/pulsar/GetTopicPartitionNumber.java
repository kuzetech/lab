package com.kuzetech.bigdata.pulsar;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;

public class GetTopicPartitionNumber {
    public static void main(String[] args) {
        String serviceHttpUrl = "http://localhost:8080"; // 替换为你的 Pulsar Admin 地址
        String topic = "persistent://public/default/my-topic"; // 替换为你的 Topic 名称

        try (PulsarAdmin admin = PulsarAdmin.builder()
                .serviceHttpUrl(serviceHttpUrl)
                .build()) {

            int partitions = admin.topics().getPartitionedTopicMetadata(topic).partitions;
            System.out.println("Partition count: " + partitions);
        } catch (PulsarAdminException | PulsarClientException e) {
            System.err.println("Failed to get partition info: " + e.getMessage());
        }
    }
}
