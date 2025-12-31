package com.kuzetech.bigdata.flink.redis.func;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

@Slf4j
public class StateProcessFunc extends ProcessFunction<String, String> implements CheckpointedFunction {

    @Override
    public void open(OpenContext openContext) throws Exception {
        log.info("StateProcessFunc open");
    }

    @Override
    public void processElement(String value, ProcessFunction<String, String>.Context ctx, Collector<String> out) throws Exception {
        out.collect(value);
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        log.info("StateProcessFunc snapshotState");
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        log.info("StateProcessFunc initializeState");
    }

    @Override
    public void close() throws Exception {
        log.info("StateProcessFunc close");
    }
}
