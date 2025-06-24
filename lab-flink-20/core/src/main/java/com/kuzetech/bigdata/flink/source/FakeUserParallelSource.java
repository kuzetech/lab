package com.kuzetech.bigdata.flink.source;

import com.kuzetech.bigdata.flink.fake.FakeUser;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;


public class FakeUserParallelSource implements ParallelSourceFunction<FakeUser> {

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<FakeUser> ctx) throws Exception {
        while (isRunning) {
            ctx.collect(FakeUser.generateUser());
            Thread.sleep(50);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
