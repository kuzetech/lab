package com.kuzetech.bigdata.flink;

import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PrintCountWindowFunction implements WindowFunction<Long, String, String, TimeWindow> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.of("Asia/Shanghai");

    @Override
    public void apply(String key, TimeWindow window, Iterable<Long> input, Collector<String> out) {
        LocalDateTime windowStart = Instant.ofEpochMilli(window.getStart())
                .atZone(zoneId)
                .toLocalDateTime();
        String windowStartStr = windowStart.format(formatter);
        LocalDateTime windowEnd = Instant.ofEpochMilli(window.getEnd())
                .atZone(zoneId)
                .toLocalDateTime();
        String windowEndStr = windowEnd.format(formatter);

        Long count = input.iterator().next();
        if (count > 0) {
            String result = String.format("Window Start: %s, Window End: %s, Event is %s, Total is: %d", windowStartStr, windowEndStr, key, count);
            out.collect(result);
        }
    }
}
