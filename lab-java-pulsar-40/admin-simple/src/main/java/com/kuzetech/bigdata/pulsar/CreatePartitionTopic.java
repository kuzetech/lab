package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.AdminUtil;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;

public class CreatePartitionTopic {
    public static void main(String[] args) {
        String topic = "persistent://public/default/funnydb-ingest-receive"; // 替换为你的 Topic 名称

        try (PulsarAdmin admin = AdminUtil.createDefaultLocalAdmin()) {
            admin.topics().createPartitionedTopic(topic, 9); // 创建一个有4个分区的分区主题
        } catch (PulsarClientException | PulsarAdminException e) {
            System.err.println("Failed to create topic : " + e.getMessage());
        }
    }
}
