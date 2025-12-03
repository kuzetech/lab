package com.kuzetech.bigdata.flink.test;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public class PassThroughProcessFunction extends ProcessFunction<Integer, Integer> {

    @Override
    public void processElement(Integer value, Context ctx, Collector<Integer> out) throws Exception {
        out.collect(value);
    }
}
