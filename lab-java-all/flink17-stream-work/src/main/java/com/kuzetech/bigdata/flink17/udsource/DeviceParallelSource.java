package com.kuzetech.bigdata.flink17.udsource;

import com.kuzetech.bigdata.flink17.udsource.model.Device;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

public class DeviceParallelSource implements ParallelSourceFunction<Device> {

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<Device> ctx) throws Exception {
        while (isRunning) {
            ctx.collect(Device.generateDevice());
            Thread.sleep(5000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
