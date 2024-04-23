package com.kuze.bigdata.kstreams.idmapping;

import com.google.gson.Gson;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class IDMappingStreams {


    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            throw new IllegalArgumentException("Must specify the path for a configuration file.");
        }

        IDMappingStreams instance = new IDMappingStreams();
        Properties envProps = instance.loadProperties(args[0]);
        Properties streamProps = instance.buildStreamsProperties(envProps);
        Topology topology = instance.buildTopology(envProps);

        instance.createTopics(envProps);

        final KafkaStreams streams = new KafkaStreams(topology, streamProps);
        final CountDownLatch latch = new CountDownLatch(1);

        // Attach shutdown handler to catch Control-C.
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    private Properties loadProperties(String propertyFilePath) throws IOException {
        Properties envProps = new Properties();
        try (FileInputStream input = new FileInputStream(propertyFilePath)) {
            envProps.load(input);
            return envProps;
        }
    }

    private Properties buildStreamsProperties(Properties envProps) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, envProps.getProperty("application.id"));
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, envProps.getProperty("bootstrap.servers"));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }

    private void createTopics(Properties envProps) {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, envProps.getProperty("bootstrap.servers"));
        try (AdminClient client = AdminClient.create(config)) {
            List<NewTopic> topics = new ArrayList<>();
            topics.add(new NewTopic(
                    envProps.getProperty("stream.topic.name"),
                    Integer.parseInt(envProps.getProperty("stream.topic.partitions")),
                    Short.parseShort(envProps.getProperty("stream.topic.replication.factor"))));

            topics.add(new NewTopic(
                    envProps.getProperty("table.topic.name"),
                    Integer.parseInt(envProps.getProperty("table.topic.partitions")),
                    Short.parseShort(envProps.getProperty("table.topic.replication.factor"))));

            client.createTopics(topics);
        }
    }

    private Topology buildTopology(Properties envProps) {
        final StreamsBuilder builder = new StreamsBuilder();
        final String streamTopic = envProps.getProperty("stream.topic.name");
        final String rekeyedTopic = envProps.getProperty("rekeyed.topic.name");
        final String tableTopic = envProps.getProperty("table.topic.name");
        final String outputTopic = envProps.getProperty("output.topic.name");
        final Gson gson = new Gson();

        // 1. 构造表
        KStream<String, IDMapping> rekeyed = builder.<String, String>stream(tableTopic)
                .mapValues(json -> gson.fromJson(json, IDMapping.class))
                .filter((noKey, idMapping) -> !Objects.isNull(idMapping.getPhone()))
                .map((noKey, idMapping) -> new KeyValue<>(idMapping.getPhone(), idMapping));
        rekeyed.to(rekeyedTopic);
        KTable<String, IDMapping> table = builder.table(rekeyedTopic);

        // 2. 流-表连接
        KStream<String, String> joinedStream = builder.<String, String>stream(streamTopic)
                .mapValues(json -> gson.fromJson(json, IDMapping.class))
                .map((noKey, idMapping) -> new KeyValue<>(idMapping.getPhone(), idMapping))
                .leftJoin(table, (value1, value2) -> IDMapping.builder()
                        .phone(value2.getPhone() == null ? value1.getPhone() : value2.getPhone())
                        .deviceId(value2.getDeviceId() == null ? value1.getDeviceId() : value2.getDeviceId())
                        .idCard(value2.getIdCard() == null ? value1.getIdCard() : value2.getIdCard())
                        .build())
                .mapValues(v -> gson.toJson(v));

        joinedStream.to(outputTopic);

        return builder.build();
    }
}

