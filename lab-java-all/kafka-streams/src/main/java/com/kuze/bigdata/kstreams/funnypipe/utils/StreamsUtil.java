package com.kuze.bigdata.kstreams.funnypipe.utils;

import com.kuze.bigdata.kstreams.funnypipe.EnrichedEventSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class StreamsUtil {

    public static Properties buildStreamsProperties(Properties envProps) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, envProps.getProperty("application.id"));
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, envProps.getProperty("bootstrap.servers"));
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, EnrichedEventSerde.class);
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, Integer.parseInt(envProps.getProperty("num.stream.threads")));
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, envProps.getProperty("processing.guarantee"));

        //props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, EnrichedEventSerde.class);
        //props.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, EnrichedEventSerde.class);
        //props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, EnrichedEventSerde.class);

        return props;
    }
}
