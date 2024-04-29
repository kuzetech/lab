package com.kuze.bigdata.kstreams.funnypipe;

import com.kuze.bigdata.kstreams.funnypipe.metadata.FileMetadataFetcher;
import com.kuze.bigdata.kstreams.funnypipe.utils.ConfUtil;
import com.kuze.bigdata.kstreams.funnypipe.utils.KafkaUtil;
import com.kuze.bigdata.kstreams.funnypipe.utils.StreamsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class PipeMain {

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || StringUtils.isEmpty(args[0])) {
            throw new IllegalArgumentException("Must specify the path for a configuration file.");
        }

        Properties envProps = ConfUtil.loadProperties(args[0]);
        Properties streamProps = StreamsUtil.buildStreamsProperties(envProps);

        FileMetadataFetcher fetcher;
        try {
            fetcher = new FileMetadataFetcher(envProps);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Topology topology = buildTopology(envProps, fetcher);

        KafkaUtil.createTopics(envProps);

        final KafkaStreams streams = new KafkaStreams(topology, streamProps);
        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                fetcher.close();
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

    private static Topology buildTopology(Properties envProps, FileMetadataFetcher fetcher) {
        Topology builder = new Topology();

        final String streamTopic = envProps.getProperty("stream.topic.name");

        StoreBuilder<KeyValueStore<byte[], EnrichedEvent>> funnyStoreSupplier =
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("cache"),
                        Serdes.ByteArray(),
                        new EnrichedEventSerde());

        builder.addSource("Source", streamTopic)
                .addProcessor("Process", () -> new FunnyDBProcessor(fetcher), "Source")
                .addStateStore(funnyStoreSupplier, "Process")
                .addSink(
                        "Sink",
                        (key, value, recordContext) -> value.getTopic(),
                        new ByteArraySerializer(),
                        new DestMessageSerializer(),
                        "Process"
                );

        return builder;
    }
}
