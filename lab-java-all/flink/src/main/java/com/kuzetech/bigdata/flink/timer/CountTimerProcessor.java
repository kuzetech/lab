package com.kuzetech.bigdata.flink.timer;

import com.kuzetech.bigdata.flink.udsource.model.KeyAndTimeEvent;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

public class CountTimerProcessor extends KeyedProcessFunction<String, KeyAndTimeEvent, CountWithTimestamp> {

    private transient ValueState<CountWithTimestamp> state;

    @Override
    public void open(Configuration parameters) throws Exception {
        state = getRuntimeContext().getState(new ValueStateDescriptor<>("myState", CountWithTimestamp.class));
    }

    @Override
    public void processElement(
            KeyAndTimeEvent value,
            KeyedProcessFunction<String, KeyAndTimeEvent, CountWithTimestamp>.Context ctx,
            Collector<CountWithTimestamp> out) throws Exception {

        CountWithTimestamp currentState = state.value();
        if (currentState == null) {
            currentState = new CountWithTimestamp(value.getKey(), 1L, ctx.timestamp());
        } else {
            currentState.increaseCount();
            currentState.setLastModified(ctx.timestamp());
        }
        state.update(currentState);
        ctx.timerService().registerEventTimeTimer(currentState.getLastModified() + 5000);
    }

    @Override
    public void onTimer(
            long timestamp,
            KeyedProcessFunction<String, KeyAndTimeEvent, CountWithTimestamp>.OnTimerContext ctx,
            Collector<CountWithTimestamp> out) throws Exception {
        CountWithTimestamp currentState = state.value();
        if (timestamp >= (currentState.getLastModified() + 5000)) {
            out.collect(currentState);
            state.clear();
        }
    }
}
