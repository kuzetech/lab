package com.kuzetech.bigdata.flink.udsource;

import com.kuzetech.bigdata.flink.udsource.model.User;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;


public class UserParallelSource implements ParallelSourceFunction<User> {

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<User> ctx) throws Exception {
        while (isRunning) {
            ctx.collect(User.generateUser());
            Thread.sleep(5000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
