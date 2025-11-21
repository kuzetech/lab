package com.kuzetech.bigdata.flink.test;

import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IncrementMapFunctionTest {

    private IncrementMapFunction func;

    @Before
    public void setUp() throws Exception {
        // instantiate your function
        func = new IncrementMapFunction();
    }

    @Test
    public void testIncrement() throws Exception {
        // call the methods that you have implemented
        assertEquals(3L, func.map(2L));
    }
}