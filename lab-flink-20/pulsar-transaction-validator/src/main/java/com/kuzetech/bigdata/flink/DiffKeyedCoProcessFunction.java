package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.funny.FunnyMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DiffKeyedCoProcessFunction extends KeyedCoProcessFunction<String, FunnyMessage, FunnyMessage, String> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.of("Asia/Shanghai");

    private ValueState<Boolean> seenInA;
    private ValueState<Boolean> seenInB;
    private ValueState<String> key;

    @Override
    public void open(OpenContext openContext) throws Exception {
        seenInA = getRuntimeContext().getState(
                new ValueStateDescriptor<>("seenInA", Boolean.class, false)
        );
        seenInB = getRuntimeContext().getState(
                new ValueStateDescriptor<>("seenInB", Boolean.class, false)
        );
        key = getRuntimeContext().getState(
                new ValueStateDescriptor<>("key", String.class)
        );
    }

    @Override
    public void processElement1(FunnyMessage value, KeyedCoProcessFunction<String, FunnyMessage, FunnyMessage, String>.Context ctx, Collector<String> out) throws Exception {
        key.update(value.getDistinctKey());
        seenInA.update(true);
        if (Boolean.TRUE.equals(seenInB.value())) {
            cleanup();
        } else {
            ctx.timerService().registerEventTimeTimer(value.getIngestTime() + 300000);
        }
    }

    @Override
    public void processElement2(FunnyMessage value, KeyedCoProcessFunction<String, FunnyMessage, FunnyMessage, String>.Context ctx, Collector<String> out) throws Exception {
        key.update(value.getDistinctKey());
        seenInB.update(true);
        if (Boolean.TRUE.equals(seenInA.value())) {
            cleanup();
        } else {
            ctx.timerService().registerEventTimeTimer(value.getIngestTime() + 300000);
        }
    }

    @Override
    public void onTimer(long timestamp, KeyedCoProcessFunction<String, FunnyMessage, FunnyMessage, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
        String keyObj = key.value();
        if (StringUtils.isNotEmpty(keyObj)) {
            LocalDateTime currentTime = Instant.ofEpochMilli(System.currentTimeMillis())
                    .atZone(zoneId)
                    .toLocalDateTime();
            String currentTimeStr = currentTime.format(formatter);
            out.collect(String.format(
                    "Missing record, index=%s, logId=%s, Current Time: %s",
                    keyObj,
                    ctx.getCurrentKey(),
                    currentTimeStr
            ));
            cleanup();
        }
    }

    private void cleanup() throws Exception {
        seenInA.clear();
        seenInB.clear();
        key.clear();
    }
}
