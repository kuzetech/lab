package com.kuzetech.bigdata.flink.test;

import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class CountWindowAverage extends RichFlatMapFunction<Tuple2<String, Long>, Tuple2<String, Long>> {

    private transient ValueState<Tuple2<Long, Long>> sum;

    @Override
    public void open(OpenContext openContext) throws Exception {
        ValueStateDescriptor<Tuple2<Long, Long>> descriptor = new ValueStateDescriptor<>(
                "average", // the state name
                TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
                }), // type information
                Tuple2.of(0L, 0L) // default value of the state, if nothing was set
        );
        sum = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void flatMap(Tuple2<String, Long> value, Collector<Tuple2<String, Long>> out) throws Exception {
        // access the state value
        Tuple2<Long, Long> currentSum = sum.value();

        // add the input value
        currentSum.f0 += value.f1;

        // update the count
        currentSum.f1 += 1;

        // update the state
        sum.update(currentSum);

        // if the count reaches 2, emit the average and clear the state
        if (currentSum.f1 >= 2) {
            out.collect(new Tuple2<>(value.f0, currentSum.f0 / currentSum.f1));
            sum.clear();
        }
    }


}
