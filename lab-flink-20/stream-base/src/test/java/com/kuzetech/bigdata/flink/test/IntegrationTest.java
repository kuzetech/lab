package com.kuzetech.bigdata.flink.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzetech.bigdata.flink.base.FlinkUtil;
import com.kuzetech.bigdata.flink.base.JobConfig;
import com.kuzetech.bigdata.flink.json.ObjectMapperInstance;
import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafka.junit.*;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class IntegrationTest {

    private final static ObjectMapper OBJECT_MAPPER = ObjectMapperInstance.getInstance();
    private final static Integer JOB_PARALLELISM = 2;

    @ClassRule
    public static MiniClusterWithClientResource flinkCluster = new MiniClusterWithClientResource(
            new MiniClusterResourceConfiguration.Builder()
                    .setNumberSlotsPerTaskManager(JOB_PARALLELISM)
                    .setNumberTaskManagers(1)
                    .build()
    );
    @ClassRule
    public static Network network = Network.newNetwork();
    @ClassRule
    public static KafkaContainer kafkaCluster = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.6"))
            .withNetwork(network)
            .withKraft()
            .withExposedPorts(9092, 9093);

    public static ExternalKafkaCluster kafkaAdmin;
    public static JobConfig jobConfig;

    @BeforeAll
    public static void setup() throws Exception {
        kafkaCluster.start();

        ParameterTool parameterTool = ParameterTool.fromPropertiesFile("src/test/resources/job.properties");
        jobConfig = new JobConfig(parameterTool);
        jobConfig.getKafkaSourceConfig().setBootstrapServers(kafkaCluster.getBootstrapServers());
        jobConfig.getKafkaSinkConfig().setBootstrapServers(kafkaCluster.getBootstrapServers());

        kafkaAdmin = ExternalKafkaCluster.at(kafkaCluster.getBootstrapServers());
        kafkaAdmin.createTopic(TopicConfig.withName(jobConfig.getKafkaSourceConfig().getTopic()).build());
        kafkaAdmin.createTopic(TopicConfig.withName(jobConfig.getKafkaSinkConfig().getTopic()).build());

        StreamExecutionEnvironment env = FlinkUtil.initEnv(parameterTool);
        env.setParallelism(JOB_PARALLELISM);
        DemoJob.buildFlow(env, jobConfig);
        JobClient jobClient = env.executeAsync();
        while (true) {
            JobStatus status = jobClient.getJobStatus().get();
            if (JobStatus.RUNNING.equals(status)) {
                log.info("flink job is running");
                break;
            }
            Thread.sleep(1000);
        }
    }

    @AfterAll
    public static void clean() throws Exception {
        flinkCluster.cancelAllJobs();
        kafkaCluster.close();
        network.close();
    }

    @Test
    public void test() throws Exception {
        List<String> events = Files.readAllLines(Paths.get("src/test/resources/events.ndjson"));
        kafkaAdmin.send(SendValues.to(jobConfig.getKafkaSourceConfig().getTopic(), events));

        List<KeyValue<String, String>> messages = kafkaAdmin.observe(ObserveKeyValues.on(jobConfig.getKafkaSinkConfig().getTopic(), events.size()));
        List<JsonNode> result = new ArrayList<>();
        for (KeyValue<String, String> message : messages) {
            Assertions.assertThat(message.getHeaders()).hasSize(0);
            Assertions.assertThat(message.getKey()).isNull();
            JsonNode data = OBJECT_MAPPER.readTree(message.getValue());
            result.add(data);
        }

        List<String> expects = Files.readAllLines(Paths.get("src/test/resources/expect.ndjson"));
        List<JsonNode> expectDataList = new ArrayList<>();
        for (String expect : expects) {
            JsonNode data = OBJECT_MAPPER.readTree(expect);
            expectDataList.add(data);
        }
        Assertions.assertThat(result).hasSize(expectDataList.size()).containsAll(expectDataList);
    }


}
