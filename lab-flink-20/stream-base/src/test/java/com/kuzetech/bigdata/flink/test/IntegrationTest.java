package com.kuzetech.bigdata.flink.test;

import net.mguenther.kafka.junit.ExternalKafkaCluster;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

public class IntegrationTest {

    @ClassRule
    public static MiniClusterWithClientResource flinkCluster =
            new MiniClusterWithClientResource(
                    new MiniClusterResourceConfiguration.Builder()
                            .setNumberSlotsPerTaskManager(2)
                            .setNumberTaskManagers(1)
                            .build());
    @ClassRule
    public static Network network = Network.newNetwork();
    @ClassRule
    public static KafkaContainer kafkaCluster = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.6"))
            .withNetwork(network)
            .withKraft()
            .withExposedPorts(9093, 9093, 19092);

    public static ExternalKafkaCluster kafkaAdmin;

    @BeforeAll
    public static void setup() throws Exception {
        kafkaAdmin = ExternalKafkaCluster.at(kafkaCluster.getBootstrapServers());
    }

    @AfterAll
    public static void clean() throws Exception {

    }


}
