package com.kuzetech.bigdata.flink.func;

import com.kuzetech.bigdata.flink.time.TimeUtil;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.LocalDateTime;

public class PrintCountWindowFunction extends ProcessWindowFunction<Long, String, String, TimeWindow> {

    @Override
    public void process(String key, ProcessWindowFunction<Long, String, String, TimeWindow>.Context context, Iterable<Long> input, Collector<String> out) throws Exception {
        Long result = input.iterator().next();
        if (count > 0 || (count == 0 && Boolean.TRUE.equals(needNotice.value()))) {
            if (count > 0) {
                needNotice.update(true);
            }

            LocalDateTime currentTime = Instant.ofEpochMilli(System.currentTimeMillis())
                    .atZone(TimeUtil.DEFAULT_ZONE_ID)
                    .toLocalDateTime();
            String currentTimeStr = currentTime.format(TimeUtil.DEFAULT_FORMATTER);
            TimeWindow window = context.window();
            LocalDateTime windowStartTime = Instant.ofEpochMilli(window.getStart())
                    .atZone(TimeUtil.DEFAULT_ZONE_ID)
                    .toLocalDateTime();
            String windowStartStr = windowStartTime.format(TimeUtil.DEFAULT_FORMATTER);
            LocalDateTime windowEndTime = Instant.ofEpochMilli(window.getEnd())
                    .atZone(TimeUtil.DEFAULT_ZONE_ID)
                    .toLocalDateTime();
            String windowEndStr = windowEndTime.format(TimeUtil.DEFAULT_FORMATTER);


            String result = String.format("Window Start: %s, Window End: %s, Key is %s, Result is: %d, Current Time: %s", windowStartStr, windowEndStr, key, count, currentTimeStr);
            out.collect(result);
        }
    }
}
