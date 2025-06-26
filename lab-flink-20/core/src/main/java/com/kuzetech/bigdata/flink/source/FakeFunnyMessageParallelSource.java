package com.kuzetech.bigdata.flink.source;

import com.kuzetech.bigdata.flink.base.CommonSourceMessage;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;


public class FakeFunnyMessageParallelSource implements ParallelSourceFunction<CommonSourceMessage> {

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<CommonSourceMessage> ctx) throws Exception {
        while (isRunning) {
            ctx.collect(CommonSourceMessage.generateMessage());
            Thread.sleep(50);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
