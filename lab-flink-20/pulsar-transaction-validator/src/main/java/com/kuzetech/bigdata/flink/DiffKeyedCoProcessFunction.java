package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.funny.FunnyMessage;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

public class DiffKeyedCoProcessFunction extends KeyedCoProcessFunction<String, FunnyMessage, FunnyMessage, String> {

    private ValueState<Boolean> seenInA;
    private ValueState<Boolean> seenInB;
    private ValueState<String> index;

    @Override
    public void open(OpenContext openContext) throws Exception {
        seenInA = getRuntimeContext().getState(
                new ValueStateDescriptor<>("seenInA", Boolean.class)
        );
        seenInB = getRuntimeContext().getState(
                new ValueStateDescriptor<>("seenInB", Boolean.class)
        );
        index = getRuntimeContext().getState(
                new ValueStateDescriptor<>("index", String.class)
        );
    }

    @Override
    public void processElement1(FunnyMessage value, KeyedCoProcessFunction<String, FunnyMessage, FunnyMessage, String>.Context ctx, Collector<String> out) throws Exception {
        index.update(value.getKey());
        seenInA.update(true);
        if (Boolean.TRUE.equals(seenInB.value())) {
            cleanup();
        } else {
            ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime() + 120000);
        }
    }

    @Override
    public void processElement2(FunnyMessage value, KeyedCoProcessFunction<String, FunnyMessage, FunnyMessage, String>.Context ctx, Collector<String> out) throws Exception {
        index.update(value.getKey());
        seenInB.update(true);
        if (Boolean.TRUE.equals(seenInA.value())) {
            cleanup();
        } else {
            ctx.timerService().registerProcessingTimeTimer(ctx.timerService().currentProcessingTime() + 120000);
        }
    }

    @Override
    public void onTimer(long timestamp, KeyedCoProcessFunction<String, FunnyMessage, FunnyMessage, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
        if (index.value() != null) {
            out.collect(String.format("Missing record, index=%s, logId=%s", index.value(), ctx.getCurrentKey()));
            cleanup();
        }
    }

    private void cleanup() throws Exception {
        seenInA.clear();
        seenInB.clear();
        index.clear();
    }
}
