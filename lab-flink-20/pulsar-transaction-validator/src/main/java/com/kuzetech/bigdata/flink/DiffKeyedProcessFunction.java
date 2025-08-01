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

public class DiffKeyedProcessFunction extends KeyedProcessFunction<String, FunnyMessage, String> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.of("Asia/Shanghai");

    private ValueState<Boolean> seenInKafka;
    private ValueState<Boolean> seenInPulsar;

    @Override
    public void open(OpenContext openContext) throws Exception {
        seenInKafka = getRuntimeContext().getState(
                new ValueStateDescriptor<>("seenInA", Boolean.class, false)
        );
        seenInPulsar = getRuntimeContext().getState(
                new ValueStateDescriptor<>("seenInB", Boolean.class, false)
        );
    }


    @Override
    public void processElement(FunnyMessage msg, KeyedProcessFunction<String, FunnyMessage, String>.Context ctx, Collector<String> out) throws Exception {
        if (FunnyMessage.CHANNEL_KEY_KAFKA.equals(msg.getChannel())) {
            seenInKafka.update(true);
        } else {
            seenInPulsar.update(true);
        }
        if (Boolean.TRUE.equals(seenInKafka.value()) && Boolean.TRUE.equals(seenInPulsar.value())) {
            cleanup();
        } else {
            ctx.timerService().registerEventTimeTimer(msg.getIngestTime() + 300000);
        }
    }

    @Override
    public void onTimer(long timestamp, KeyedProcessFunction<String, FunnyMessage, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
        if (Boolean.TRUE.equals(seenInKafka.value()) || Boolean.TRUE.equals(seenInPulsar.value())) {
            String missChan = Boolean.TRUE.equals(seenInKafka.value()) ? FunnyMessage.CHANNEL_KEY_PULSAR : FunnyMessage.CHANNEL_KEY_KAFKA;
            LocalDateTime currentTime = Instant.ofEpochMilli(System.currentTimeMillis())
                    .atZone(zoneId)
                    .toLocalDateTime();
            String currentTimeStr = currentTime.format(formatter);
            out.collect(String.format(
                    "Missing record in %s, index=%s, Current Time: %s",
                    missChan,
                    ctx.getCurrentKey(),
                    currentTimeStr
            ));
        }
        cleanup();
    }

    private void cleanup() throws Exception {
        seenInKafka.clear();
        seenInPulsar.clear();
    }
}
