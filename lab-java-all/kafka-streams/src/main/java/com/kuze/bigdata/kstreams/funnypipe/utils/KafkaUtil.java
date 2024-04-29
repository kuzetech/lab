package com.kuze.bigdata.kstreams.funnypipe.utils;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.*;

public class KafkaUtil {

    public static void createTopics(Properties envProps) {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, envProps.getProperty("bootstrap.servers"));
        try (AdminClient client = AdminClient.create(config)) {
            List<NewTopic> topics = new ArrayList<>();
            topics.add(new NewTopic(
                    envProps.getProperty("stream.topic.name"),
                    Integer.parseInt(envProps.getProperty("stream.topic.partitions")),
                    Short.parseShort(envProps.getProperty("stream.topic.replication.factor"))));

            client.createTopics(topics);
        }
    }
}
