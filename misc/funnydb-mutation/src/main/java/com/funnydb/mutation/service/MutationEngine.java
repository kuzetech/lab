package com.funnydb.mutation.service;

import com.funnydb.mutation.model.MutationData;
import com.funnydb.mutation.model.MutationEvent;
import com.funnydb.mutation.model.MutationTargetType;
import com.funnydb.mutation.model.StoredDocument;

import java.util.LinkedHashMap;
import java.util.Map;

public class MutationEngine {

    public Map<String, Object> buildSnapshot(MutationEvent event, StoredDocument document, long updatedTime) {
        MutationTargetType targetType = MutationTargetType.fromEventType(event.getType());
        Map<String, Object> snapshot = new LinkedHashMap<>(document.getData());
        snapshot.put("#event_time", document.getMetadata().getLastEventTime());
        snapshot.put("#updated_time", updatedTime);
        snapshot.put("#created_time", document.getMetadata().getCreatedTime());
        snapshot.put("#data_lifecycle", document.getMetadata().getDataLifecycle());
        snapshot.put(targetType.getIdentifyField(), event.getData().getIdentify());
        return snapshot;
    }

    public MutationRouting buildRouting(MutationEvent event) {
        MutationTargetType targetType = MutationTargetType.fromEventType(event.getType());
        MutationData data = event.getData();
        String redisKey = String.format("%s:%s:%s:%s",
                event.getApp(),
                data.getDataLifecycle(),
                targetType.getRedisSegment(),
                data.getIdentify());
        String outputTopic = String.format("%s-flink-%s", event.getApp(), targetType.getTopicSegment());
        return new MutationRouting(redisKey, outputTopic, data.getIdentify());
    }
}
