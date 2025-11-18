package com.kuzetech.bigdata.flink.udsource;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class GenTimeSingleParallelSource implements SourceFunction<Long> {

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<Long> ctx) throws Exception {
        while (isRunning) {
            ctx.collect(System.currentTimeMillis());
            Thread.sleep(250);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
