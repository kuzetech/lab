package com.kuzetech.bigdata.flink.pulsar.domain;

import org.apache.flink.connector.pulsar.source.enumerator.cursor.CursorPosition;
import org.apache.flink.connector.pulsar.source.enumerator.cursor.StartCursor;
import org.apache.pulsar.client.api.MessageId;

import java.util.HashMap;
import java.util.Map;

public class MessageIdMapStartCursor implements StartCursor {

    private final MessageId defaultMessageId;
    private final Map<String, MessageId> messageIdMap;

    public MessageIdMapStartCursor(MessageId defaultMessageId, Map<String, MessageId> messageIdMap) {
        this.defaultMessageId = defaultMessageId;
        if (messageIdMap == null) {
            this.messageIdMap = new HashMap<>();
        } else {
            this.messageIdMap = messageIdMap;
        }
    }

    public static String generateMessageIdMapKey(String topic, int partitionId) {
        return topic + "_" + partitionId;
    }

    @Override
    public CursorPosition position(String topic, int partitionId) {
        MessageId messageId = this.messageIdMap.getOrDefault(generateMessageIdMapKey(topic, partitionId), defaultMessageId);
        return new CursorPosition(messageId, true);
    }
}
