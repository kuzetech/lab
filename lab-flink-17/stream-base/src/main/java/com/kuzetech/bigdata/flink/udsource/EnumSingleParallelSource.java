package com.kuzetech.bigdata.flink.udsource;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnumSingleParallelSource implements SourceFunction<String> {

    private static List<String> appList = new ArrayList<>();

    static {
        appList.add("app1");
        appList.add("app2");
        appList.add("app3");
        appList.add("app4");
        appList.add("app5");
        appList.add("app6");
    }

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        Random r = new Random();
        while (isRunning) {
            int index = r.nextInt(appList.size());
            String app = appList.get(index);
            ctx.collect(app);
            Thread.sleep(250);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
