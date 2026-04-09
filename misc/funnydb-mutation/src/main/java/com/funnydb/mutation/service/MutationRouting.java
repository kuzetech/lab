package com.funnydb.mutation.service;

public class MutationRouting {
    private final String redisKey;
    private final String outputTopic;
    private final String outputKey;

    public MutationRouting(String redisKey, String outputTopic, String outputKey) {
        this.redisKey = redisKey;
        this.outputTopic = outputTopic;
        this.outputKey = outputKey;
    }

    public String getRedisKey() {
        return redisKey;
    }

    public String getOutputTopic() {
        return outputTopic;
    }

    public String getOutputKey() {
        return outputKey;
    }
}
