package com.kuzetech.bigdata.flink.udsource;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class StringNoParallelSource implements SourceFunction<String> {

    private int count = 0;

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        while (isRunning) {
            if (count < 4) {
                ctx.collect("key-" + count);
                count++;
            }
            Thread.sleep(100);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
