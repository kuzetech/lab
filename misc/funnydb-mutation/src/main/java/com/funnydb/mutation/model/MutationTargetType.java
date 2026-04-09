package com.funnydb.mutation.model;

public enum MutationTargetType {
    USER("UserMutation", "user", "users", "#user_id"),
    DEVICE("DeviceMutation", "device", "devices", "#device_id");

    private final String eventType;
    private final String redisSegment;
    private final String topicSegment;
    private final String identifyField;

    MutationTargetType(String eventType, String redisSegment, String topicSegment, String identifyField) {
        this.eventType = eventType;
        this.redisSegment = redisSegment;
        this.topicSegment = topicSegment;
        this.identifyField = identifyField;
    }

    public String getRedisSegment() {
        return redisSegment;
    }

    public String getTopicSegment() {
        return topicSegment;
    }

    public String getIdentifyField() {
        return identifyField;
    }

    public static MutationTargetType fromEventType(String eventType) {
        for (MutationTargetType targetType : values()) {
            if (targetType.eventType.equals(eventType)) {
                return targetType;
            }
        }
        return null;
    }
}
