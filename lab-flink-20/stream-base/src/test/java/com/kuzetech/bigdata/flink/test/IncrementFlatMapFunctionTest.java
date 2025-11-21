package com.kuzetech.bigdata.flink.test;

import org.apache.flink.util.Collector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class IncrementFlatMapFunctionTest {

    private IncrementFlatMapFunction func;

    @Before
    public void setUp() throws Exception {
        // instantiate your function
        func = new IncrementFlatMapFunction();
    }

    @Test
    public void testIncrement() throws Exception {
        // 为指定的类创建一个模拟对象。这个对象的所有方法默认返回null、0或false等默认值
        Collector<Long> collector = mock(Collector.class);

        // call the methods that you have implemented
        func.flatMap(2L, collector);

        // 验证该对象被调用的次数，以及调用的方法
        Mockito.verify(collector, times(1)).collect(3L);
    }
}