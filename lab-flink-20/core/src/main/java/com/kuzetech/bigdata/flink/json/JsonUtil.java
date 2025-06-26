package com.kuzetech.bigdata.flink.json;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class JsonUtil {
    private static final String FIELD_NAME_KEY_INGEST_TIME = "\"ingest_time\":";

    public static long extractFunnyDbIngestTime(byte[] jsonBytesData) {
        String jsonString = new String(jsonBytesData, StandardCharsets.UTF_8);
        int startIndex = jsonString.indexOf(FIELD_NAME_KEY_INGEST_TIME);
        if (startIndex == -1) {
            throw new RuntimeException(String.format("ingest_time 不存在，数据为：%s", jsonString));
        }
        startIndex += FIELD_NAME_KEY_INGEST_TIME.length();
        int endIndex = jsonString.indexOf(',', startIndex);
        if (endIndex == -1) {
            endIndex = jsonString.indexOf('}', startIndex);
        }

        String eventTimeStr = jsonString.substring(startIndex, endIndex).trim();
        if (eventTimeStr.length() > 13) {
            eventTimeStr = eventTimeStr.substring(0, 13);
        }

        try {
            return Long.parseLong(eventTimeStr);
        } catch (Exception e) {
            throw new RuntimeException(String.format("提取时间失败，数据为：%s", jsonString));
        }
    }
}
