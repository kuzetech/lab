package com.kuzetech.bigdata.flink17.udsource;

import com.kuzetech.bigdata.flink17.udsource.model.Event;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * 自定义实现并行度为1的source，模拟产生从1开始的递增数字
 */
public class EventNoParallelSource implements SourceFunction<Event> {

    private long count = 1L;

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<Event> ctx) throws Exception {
        while (isRunning) {
            Event event = new Event();
            event.setEventId(count);
            ctx.collect(event);
            count++;
            //每秒产生一条数据
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
