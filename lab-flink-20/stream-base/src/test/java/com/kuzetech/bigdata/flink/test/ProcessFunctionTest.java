package com.kuzetech.bigdata.flink.test;


import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Collections;

public class ProcessFunctionTest {

    private OneInputStreamOperatorTestHarness<Integer, Integer> harness;
    private PassThroughProcessFunction func;

    @Before
    public void setUp() throws Exception {
        func = new PassThroughProcessFunction();

        harness = ProcessFunctionTestHarnesses.forProcessFunction(func);

        // 不设置环境
        // harness.getExecutionConfig().setAutoWatermarkInterval(50);

        // 没有在 open 方法中初始化数据，所以不需要执行
        // harness.open();
    }

    @Test
    public void testPassThrough() throws Exception {
        //push (timestamped) elements into the operator (and hence user defined function)
        harness.processElement(1, 10);

        //retrieve list of emitted records for assertions
        Assertions.assertEquals(harness.extractOutputValues(), Collections.singletonList(1));
    }

}
