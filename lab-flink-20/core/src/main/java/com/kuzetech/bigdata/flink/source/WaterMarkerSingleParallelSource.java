package com.kuzetech.bigdata.flink.source;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class WaterMarkerSingleParallelSource implements SourceFunction<Long> {

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<Long> ctx) throws Exception {
        while (isRunning) {
            long fiveSecondWindow = (System.currentTimeMillis() / 5000) * 5000;
            ctx.collect(fiveSecondWindow);
            Thread.sleep(5000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
