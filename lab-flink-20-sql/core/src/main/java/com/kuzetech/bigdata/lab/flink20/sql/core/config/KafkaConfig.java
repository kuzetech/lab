package com.kuzetech.bigdata.lab.flink20.sql.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.java.utils.ParameterTool;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaConfig {
    private String topic;
    private String groupId;
    private String bootstrapServers;

    public static KafkaConfig getInstance(ParameterTool tool) {
        return new KafkaConfig(
                tool.get("connector.kafka.topic", "test"),
                tool.get("connector.kafka.group.id", "test"),
                tool.get("connector.kafka.bootstrap.servers", "localhost:9092")
        );
    }
}
