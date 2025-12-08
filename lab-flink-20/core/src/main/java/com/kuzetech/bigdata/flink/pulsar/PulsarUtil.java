package com.kuzetech.bigdata.flink.pulsar;

import com.kuzetech.bigdata.flink.pulsar.domain.MessageIdMapStartCursor;
import com.kuzetech.bigdata.flink.pulsar.domain.PulsarSourceMessage;
import com.kuzetech.bigdata.flink.pulsar.serialization.PulsarSourceMessageSerializationSchema;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.pulsar.sink.PulsarSink;
import org.apache.flink.connector.pulsar.sink.PulsarSinkBuilder;
import org.apache.flink.connector.pulsar.sink.PulsarSinkOptions;
import org.apache.flink.connector.pulsar.source.PulsarSource;
import org.apache.flink.connector.pulsar.source.PulsarSourceBuilder;
import org.apache.flink.connector.pulsar.source.PulsarSourceOptions;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StartCursor;
import org.apache.flink.connector.pulsar.source.reader.deserializer.PulsarDeserializationSchema;
import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.internal.DefaultImplementation;

import java.util.HashMap;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class PulsarUtil {

    public static final Long DEFAULT_PULSAR_PARTITION_DISCOVERY_INTERVAL_MS = 30000L;
    public static final String PULSAR_START_CURSOR_EARLIEST = "earliest";
    public static final String PULSAR_START_CURSOR_LATEST = "latest";

    public static String getDefaultCompleteTopicName(String simpleName) {
        if (simpleName.startsWith("persistent:")) {
            return simpleName;
        }
        return "persistent://public/default/" + simpleName;
    }

    public static <T> PulsarSourceBuilder<T> buildSourceBaseBuilder(PulsarSourceConfig config, PulsarDeserializationSchema<T> deserializationSchema) {
        String completeTopicName = getDefaultCompleteTopicName(config.getTopic());
        return PulsarSource.builder()
                .setServiceUrl(config.getServiceUrl())
                .setStartCursor(getJobStartCursor(completeTopicName, config.getStartCursor()))
                .setTopics(config.getTopic())
                .setDeserializationSchema(deserializationSchema)
                .setSubscriptionName(config.getSubscriber())
                .setConfig(PulsarSourceOptions.PULSAR_PARTITION_DISCOVERY_INTERVAL_MS, DEFAULT_PULSAR_PARTITION_DISCOVERY_INTERVAL_MS)
                .setConfig(PulsarSourceOptions.PULSAR_ENABLE_SOURCE_METRICS, true);
    }

    public static PulsarSinkBuilder<PulsarSourceMessage> buildSinkBaseBuilder(PulsarSinkConfig config) {
        return PulsarSink.builder()
                .setServiceUrl(config.getServiceUrl())
                .setAdminUrl(config.getAdminUrl())
                .setTopics(config.getTopic())
                .setProducerName(config.getProducerName())
                .setSerializationSchema(new PulsarSourceMessageSerializationSchema())
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setConfig(PulsarSinkOptions.PULSAR_TOPIC_METADATA_REFRESH_INTERVAL, DEFAULT_PULSAR_PARTITION_DISCOVERY_INTERVAL_MS)
                .setConfig(PulsarSinkOptions.PULSAR_ENABLE_SINK_METRICS, true)
                .setConfig(PulsarSinkOptions.PULSAR_COMPRESSION_TYPE, CompressionType.LZ4)
                .setConfig(PulsarSinkOptions.PULSAR_BATCHING_MAX_PUBLISH_DELAY_MICROS, MILLISECONDS.toMicros(50))
                .setConfig(PulsarSinkOptions.PULSAR_BATCHING_MAX_BYTES, 10 * 1024 * 1024);
    }


    private static StartCursor getJobStartCursor(String completeTopicName, String startCursor) {
        if (StringUtils.isBlank(startCursor)) {
            return StartCursor.earliest();
        }
        if (PULSAR_START_CURSOR_EARLIEST.equalsIgnoreCase(startCursor)) {
            return StartCursor.earliest();
        }
        if (PULSAR_START_CURSOR_LATEST.equalsIgnoreCase(startCursor)) {
            return StartCursor.latest();
        }

        return analysisStartCursorStr(completeTopicName, startCursor);
    }

    private static StartCursor analysisStartCursorStr(String completeTopicName, String startCursorStr) {
        Map<String, MessageId> messageIdMap = new HashMap<>();
        String[] partitionStartCursorStrArray = startCursorStr.split(",");
        for (String partitionStartCursorStr : partitionStartCursorStrArray) {
            String[] positionInfoStr = partitionStartCursorStr.split(":");
            if (positionInfoStr.length == 3) {
                long ledgerId = Long.parseLong(positionInfoStr[0]);
                long entryId = Long.parseLong(positionInfoStr[1]);
                int partitionIndex = Integer.parseInt(positionInfoStr[2]);
                messageIdMap.put(
                        MessageIdMapStartCursor.generateMessageIdMapKey(completeTopicName, partitionIndex),
                        DefaultImplementation.getDefaultImplementation().newMessageId(ledgerId, entryId, partitionIndex)
                );
            }
        }
        return new MessageIdMapStartCursor(MessageId.earliest, messageIdMap);
    }


}
