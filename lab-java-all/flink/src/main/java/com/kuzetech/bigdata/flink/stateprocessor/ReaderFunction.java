package com.kuzetech.bigdata.flink.stateprocessor;

import com.kuzetech.bigdata.flink.stateprocessor.model.DeviceModelStatistics;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

public class ReaderFunction extends KeyedStateReaderFunction<String, KeyedState> {

    ValueState<DeviceModelStatistics> state;

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<DeviceModelStatistics> stateDescriptor =
                new ValueStateDescriptor<>("model-count", Types.POJO(DeviceModelStatistics.class));
        state = getRuntimeContext().getState(stateDescriptor);
    }

    @Override
    public void readKey(String key, Context ctx, Collector<KeyedState> out) throws Exception {
        KeyedState data = new KeyedState();
        data.setStatistics(state.value());
        out.collect(data);
    }
}
