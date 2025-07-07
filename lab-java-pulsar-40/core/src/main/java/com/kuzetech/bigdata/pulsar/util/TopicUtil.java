package com.kuzetech.bigdata.pulsar.util;

public class TopicUtil {
    public static String getDefaultCompleteTopic(String topic) {
        return "public/default/" + topic;
    }
}
