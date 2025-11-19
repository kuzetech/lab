package com.kuzetech.bigdata.flink.func;

import com.kuzetech.bigdata.flink.time.TimeUtil;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

public class PrintDiffLogIdWindowFunction implements WindowFunction<Set<String>, String, String, TimeWindow> {

    @Override
    public void apply(String key, TimeWindow window, Iterable<Set<String>> input, Collector<String> out) {
        Set<String> diffLogIdSet = input.iterator().next();
        if (!diffLogIdSet.isEmpty()) {
            LocalDateTime windowStart = Instant.ofEpochMilli(window.getStart())
                    .atZone(TimeUtil.DEFAULT_ZONE_ID)
                    .toLocalDateTime();
            String windowStartStr = windowStart.format(TimeUtil.DEFAULT_FORMATTER);
            LocalDateTime windowEnd = Instant.ofEpochMilli(window.getEnd())
                    .atZone(TimeUtil.DEFAULT_ZONE_ID)
                    .toLocalDateTime();
            String windowEndStr = windowEnd.format(TimeUtil.DEFAULT_FORMATTER);
            LocalDateTime currentTime = Instant.ofEpochMilli(System.currentTimeMillis())
                    .atZone(TimeUtil.DEFAULT_ZONE_ID)
                    .toLocalDateTime();
            String currentTimeStr = currentTime.format(TimeUtil.DEFAULT_FORMATTER);
            String result = String.format("Current Time: %s, Window Start: %s, Window End: %s, Key is %s, Result is: %s, ", currentTimeStr, windowStartStr, windowEndStr, key, String.join("|", diffLogIdSet));
            out.collect(result);
        }
    }
}
