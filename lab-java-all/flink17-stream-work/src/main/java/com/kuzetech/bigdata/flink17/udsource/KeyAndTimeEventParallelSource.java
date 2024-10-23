package com.kuzetech.bigdata.flink17.udsource;

import com.kuzetech.bigdata.flink17.udsource.model.KeyAndTimeEvent;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

public class KeyAndTimeEventParallelSource implements ParallelSourceFunction<KeyAndTimeEvent> {

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<KeyAndTimeEvent> ctx) throws Exception {
        while (isRunning) {
            ctx.collect(KeyAndTimeEvent.generate());
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
