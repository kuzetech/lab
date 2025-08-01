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

    // 如果采用 Integer 类型，状态会大很多
    private ValueState<Boolean> seenAlreadyState;

    @Override
    public void open(OpenContext openContext) throws Exception {
        seenAlreadyState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("seenAlready", Boolean.class, false)
        );
    }


    @Override
    public void processElement(FunnyMessage msg, KeyedProcessFunction<String, FunnyMessage, String>.Context ctx, Collector<String> out) throws Exception {
        Boolean seenAlready = seenAlreadyState.value();
        if (seenAlready == null || Boolean.FALSE.equals(seenAlready)) {
            seenAlreadyState.update(true);
            ctx.timerService().registerEventTimeTimer(msg.getIngestTime() + 300000);
        } else {
            seenAlreadyState.clear();
            LocalDateTime currentTime = Instant.ofEpochMilli(System.currentTimeMillis())
                    .atZone(zoneId)
                    .toLocalDateTime();
            String currentTimeStr = currentTime.format(formatter);
            out.collect(String.format(
                    "Duplicate record, index=%s, Current Time: %s",
                    ctx.getCurrentKey(),
                    currentTimeStr
            ));
        }
    }

    @Override
    public void onTimer(long timestamp, KeyedProcessFunction<String, FunnyMessage, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
        seenAlreadyState.clear();
    }
}
