package com.kuzetech.bigdata.flink.test;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IncrementMapFunctionTest {

    @Test
    public void testIncrement() throws Exception {
        // instantiate your function
        IncrementMapFunction incrementer = new IncrementMapFunction();

        // call the methods that you have implemented
        assertEquals(3L, incrementer.map(2L));
    }
}