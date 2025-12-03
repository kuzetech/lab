package com.kuzetech.bigdata.flink.test;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.operators.StreamFlatMap;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.util.OutputTag;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class KeyedStateFunctionTest {

    private OneInputStreamOperatorTestHarness<Tuple2<String, Long>, Tuple2<String, Long>> harness;
    private CountWindowAverage func;

    @Before
    public void setUp() throws Exception {
        //instantiate user-defined function
        func = new CountWindowAverage();

        // wrap user defined function into a the corresponding operator
        harness = new KeyedOneInputStreamOperatorTestHarness<>(new StreamFlatMap<>(func), v -> v.f0, Types.STRING);

        // optionally configured the execution environment
        harness.getExecutionConfig().setAutoWatermarkInterval(50);

        // open the test harness (will also call open() on RichFunctions)
        harness.open();
    }

    @Test
    public void flatMap() throws Exception {
        //push (process timestamped) elements into the operator (and hence user defined function)
        harness.processElement(Tuple2.of("app1", 2L), 50L);
        harness.processElement(Tuple2.of("app1", 2L), 60L);

        //trigger event time timers by advancing the event time of the operator with a watermark
        harness.processWatermark(70L);

        //trigger processing time timers by advancing the processing time of the operator directly
        harness.setProcessingTime(110L);

        harness.processElement(Tuple2.of("app1", 2L), 210L);
        harness.processElement(Tuple2.of("app1", 2L), 220L);

        // 包含 StreamRecord 类型的封装数据和 Watermark
        Assertions.assertThat(harness.getOutput())
                .hasSize(3)
                .containsExactlyInAnyOrder(
                        new StreamRecord<>(Tuple2.of("app1", 2L), 60),
                        new Watermark(70),
                        new StreamRecord<>(Tuple2.of("app1", 2L), 220)
                );

        // 仅包含 StreamRecord 类型的封装数据
        Assertions.assertThat(harness.extractOutputStreamRecords())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        new StreamRecord<>(Tuple2.of("app1", 2L), 60),
                        new StreamRecord<>(Tuple2.of("app1", 2L), 220)
                );

        // 仅包含 StreamRecord 类型的封装数据
        Assertions.assertThat(harness.extractOutputValues())
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        Tuple2.of("app1", 2L),
                        Tuple2.of("app1", 2L)
                );


        //retrieve list of records emitted to a specific side output for assertions (ProcessFunction only)
        TypeInformation<Tuple2<String, Long>> typeInformation = TypeInformation.of(new TypeHint<Tuple2<String, Long>>() {
        });
        OutputTag<Tuple2<String, Long>> tag = new OutputTag<>("invalidRecords", typeInformation);
        Assertions.assertThat(harness.getSideOutput(tag))
                .isNull();
    }
}