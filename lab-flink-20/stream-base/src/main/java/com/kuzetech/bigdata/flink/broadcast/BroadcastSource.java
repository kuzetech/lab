package com.kuzetech.bigdata.flink.broadcast;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

public class BroadcastSource extends RichSourceFunction<Long> {

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<Long> ctx) throws Exception {
        while (isRunning) {
            ctx.collect(System.currentTimeMillis());
            Thread.sleep(5000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
