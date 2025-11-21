package com.kuzetech.bigdata.flink.test;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.operators.StreamFlatMap;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.junit.Before;
import org.junit.Test;

public class CountWindowAverageTest {

    private OneInputStreamOperatorTestHarness<Tuple2<String, Long>, Tuple2<String, Long>> testHarness;
    private CountWindowAverage statefulFlatMapFunction;

    @Before
    public void setUp() throws Exception {
        //instantiate user-defined function
        statefulFlatMapFunction = new CountWindowAverage();

        // wrap user defined function into a the corresponding operator
        testHarness = new OneInputStreamOperatorTestHarness<>(new StreamFlatMap<>(statefulFlatMapFunction));

        // optionally configured the execution environment
        testHarness.getExecutionConfig().setAutoWatermarkInterval(50);

        // open the test harness (will also call open() on RichFunctions)
        testHarness.open();
    }

    @Test
    public void flatMap() throws Exception {
        //push (timestamped) elements into the operator (and hence user defined function)
        testHarness.processElement(Tuple2.of("app1", 2L), 50L);
        testHarness.processElement(Tuple2.of("app1", 2L), 100L);

        //trigger event time timers by advancing the event time of the operator with a watermark
        testHarness.processWatermark(100L);

        //trigger processing time timers by advancing the processing time of the operator directly
        testHarness.setProcessingTime(100L);

        //retrieve list of emitted records for assertions
        assertThat(testHarness.getOutput(), containsInExactlyThisOrder(3L));

        //retrieve list of records emitted to a specific side output for assertions (ProcessFunction only)
        //assertThat(testHarness.getSideOutput(new OutputTag<>("invalidRecords")), hasSize(0))
    }
}