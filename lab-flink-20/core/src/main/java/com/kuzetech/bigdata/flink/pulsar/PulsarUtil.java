package com.kuzetech.bigdata.flink.pulsar;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.connector.pulsar.source.PulsarSource;
import org.apache.flink.connector.pulsar.source.PulsarSourceBuilder;
import org.apache.flink.connector.pulsar.source.PulsarSourceOptions;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StartCursor;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.internal.DefaultImplementation;

import java.util.HashMap;
import java.util.Map;

public class PulsarUtil {

    public static final Long DEFAULT_PULSAR_PARTITION_DISCOVERY_INTERVAL_MS = 30000L;
    public static final String PULSAR_START_CURSOR_EARLIEST = "earliest";
    public static final String PULSAR_START_CURSOR_LATEST = "latest";

    public static String getDefaultCompleteTopicName(String simpleName) {
        return "persistent://public/default/" + simpleName;
    }

    public static PulsarSourceBuilder<PulsarMessage> buildPulsarSourceBaseBuilder(PulsarConfig config) {
        String completeTopicName = getDefaultCompleteTopicName(config.getSourceTopic());
        return PulsarSource.builder()
                .setConfig(PulsarSourceOptions.PULSAR_PARTITION_DISCOVERY_INTERVAL_MS, DEFAULT_PULSAR_PARTITION_DISCOVERY_INTERVAL_MS)
                .setConfig(PulsarSourceOptions.PULSAR_ENABLE_SOURCE_METRICS, true)
                .setServiceUrl(config.getServiceUrl())
                .setStartCursor(getJobStartCursor(completeTopicName, config.getStartCursor()))
                .setTopics(completeTopicName)
                .setDeserializationSchema(new PulsarMessageDeserializationSchema())
                .setSubscriptionName(config.getSubscriber());
    }


    private static StartCursor getJobStartCursor(String completeTopicName, String startCursor) {
        if (StringUtils.isBlank(startCursor)){
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
