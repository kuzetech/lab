package com.kuzetech.bigdata.flink.stateprocessor;

import com.kuzetech.bigdata.flink.stateprocessor.model.DeviceModelStatistics;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;

public class KeyedStateBootstrapper extends KeyedStateBootstrapFunction<String, KeyedState> {

    private ValueState<DeviceModelStatistics> state;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<DeviceModelStatistics> descriptor =
                new ValueStateDescriptor<>("model-count", Types.POJO(DeviceModelStatistics.class));
        state = getRuntimeContext().getState(descriptor);
    }

    @Override
    public void processElement(KeyedState value, Context ctx) throws Exception {
        state.update(value.getStatistics());
    }
}