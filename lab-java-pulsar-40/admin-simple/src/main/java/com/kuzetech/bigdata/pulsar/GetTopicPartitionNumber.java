package com.kuzetech.bigdata.pulsar;

import com.kuzetech.bigdata.pulsar.util.AdminUtil;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;

public class GetTopicPartitionNumber {
    public static void main(String[] args) {
        String topic = "persistent://public/default/funnydb-ingest-receive";

        try (PulsarAdmin admin = AdminUtil.createDefaultLocalAdmin()) {
            int partitions = admin.topics().getPartitionedTopicMetadata(topic).partitions;
            System.out.println("Partition count: " + partitions);
        } catch (PulsarAdminException | PulsarClientException e) {
            System.err.println("Failed to get partition info: " + e.getMessage());
        }
    }
}
