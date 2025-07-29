package com.kuzetech.bigdata.flink;

import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class PrintDiffLogIdWindowFunction implements WindowFunction<Set<String>, String, String, TimeWindow> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.of("Asia/Shanghai");

    @Override
    public void apply(String key, TimeWindow window, Iterable<Set<String>> input, Collector<String> out) {
        Set<String> diffLogIdSet = input.iterator().next();
        if (!diffLogIdSet.isEmpty()) {
            LocalDateTime windowStart = Instant.ofEpochMilli(window.getStart())
                    .atZone(zoneId)
                    .toLocalDateTime();
            String windowStartStr = windowStart.format(formatter);
            LocalDateTime windowEnd = Instant.ofEpochMilli(window.getEnd())
                    .atZone(zoneId)
                    .toLocalDateTime();
            String windowEndStr = windowEnd.format(formatter);
            LocalDateTime currentTime = Instant.ofEpochMilli(System.currentTimeMillis())
                    .atZone(zoneId)
                    .toLocalDateTime();
            String currentTimeStr = currentTime.format(formatter);
            String result = String.format("Current Time: %s, Window Start: %s, Window End: %s, Key is %s, Result is: %s, ", currentTimeStr, windowStartStr, windowEndStr, key, String.join("|", diffLogIdSet));
            out.collect(result);
        }
    }
}
