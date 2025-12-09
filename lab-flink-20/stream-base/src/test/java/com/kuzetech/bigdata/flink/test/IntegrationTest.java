package com.kuzetech.bigdata.flink.test;

import com.kuzetech.bigdata.flink.base.FlinkUtil;
import com.kuzetech.bigdata.flink.base.JobConfig;
import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafka.junit.ExternalKafkaCluster;
import net.mguenther.kafka.junit.ObserveKeyValues;
import net.mguenther.kafka.junit.SendValues;
import net.mguenther.kafka.junit.TopicConfig;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class IntegrationTest {

    @ClassRule
    public static MiniClusterWithClientResource flinkCluster = new MiniClusterWithClientResource(
            new MiniClusterResourceConfiguration.Builder()
                    .setNumberSlotsPerTaskManager(2)
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
        env.setParallelism(2);
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
        kafkaAdmin.send(SendValues.to(jobConfig.getKafkaSourceConfig().getTopic(), "1"));
        kafkaAdmin.observe(ObserveKeyValues.on(jobConfig.getKafkaSinkConfig().getTopic(), 1));
    }


}
