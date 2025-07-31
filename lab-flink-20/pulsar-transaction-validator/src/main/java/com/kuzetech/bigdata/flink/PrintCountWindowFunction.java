package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.funny.FunnyMessageStatistician;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PrintCountWindowFunction extends ProcessWindowFunction<FunnyMessageStatistician, String, String, TimeWindow> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.of("Asia/Shanghai");
    private ValueState<Boolean> needNotice;

    @Override
    public void open(OpenContext openContext) throws Exception {
        needNotice = getRuntimeContext().getState(
                new ValueStateDescriptor<>("needNotice", Boolean.class, false)
        );
    }

    @Override
    public void clear(ProcessWindowFunction<FunnyMessageStatistician, String, String, TimeWindow>.Context context) throws Exception {
        needNotice.clear();
    }

    @Override
    public void process(String key, ProcessWindowFunction<FunnyMessageStatistician, String, String, TimeWindow>.Context context, Iterable<FunnyMessageStatistician> input, Collector<String> out) throws Exception {
        FunnyMessageStatistician statistician = input.iterator().next();
        Long count = statistician.getResult();
        if (count > 0 || (count == 0 && Boolean.TRUE.equals(needNotice.value()))) {
            if (count > 0) {
                needNotice.update(true);
            }
            TimeWindow window = context.window();
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
            String result = String.format("Window Start: %s, Window End: %s, Key is %s, Result is: %d, Current Time: %s", windowStartStr, windowEndStr, key, count, currentTimeStr);
            out.collect(result);
        }
    }
}
