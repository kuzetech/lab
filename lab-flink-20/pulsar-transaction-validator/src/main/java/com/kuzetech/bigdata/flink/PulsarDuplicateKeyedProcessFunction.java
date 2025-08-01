package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.funny.FunnyMessage;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PulsarDuplicateKeyedProcessFunction extends KeyedProcessFunction<String, FunnyMessage, String> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.of("Asia/Shanghai");

    private ValueState<Integer> seenTimesState;

    @Override
    public void open(OpenContext openContext) throws Exception {
        seenTimesState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("seenTimes", Integer.class, 0)
        );
    }


    @Override
    public void processElement(FunnyMessage msg, KeyedProcessFunction<String, FunnyMessage, String>.Context ctx, Collector<String> out) throws Exception {
        Integer seenTimes = seenTimesState.value();
        if (seenTimes == 0) {
            ctx.timerService().registerEventTimeTimer(msg.getIngestTime() + 300000);
        }
        seenTimesState.update(seenTimes + 1);
    }

    @Override
    public void onTimer(long timestamp, KeyedProcessFunction<String, FunnyMessage, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
        Integer seenTimes = seenTimesState.value();
        if (seenTimes > 1) {
            LocalDateTime currentTime = Instant.ofEpochMilli(System.currentTimeMillis())
                    .atZone(zoneId)
                    .toLocalDateTime();
            String currentTimeStr = currentTime.format(formatter);
            out.collect(String.format(
                    "Duplicate record, index=%s, count=%d, Current Time: %s",
                    ctx.getCurrentKey(),
                    seenTimes,
                    currentTimeStr
            ));
        }
        seenTimesState.clear();
    }
}
