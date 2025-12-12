package com.kuzetech.bigdata.flink.function;

import com.kuzetech.bigdata.flink.domain.IdentifyNewOperatorKeyedState;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

public class IdentifyNewOperatorKeyedStateReaderFunction extends KeyedStateReaderFunction<String, IdentifyNewOperatorKeyedState> {

    private transient ValueState<Long> createdTsState;

    @Override
    public void open(Configuration configuration) throws Exception {
        // 创建时间戳
        ValueStateDescriptor<Long> initCreatedTsStateDesc = new ValueStateDescriptor<>("created-ts-state", Types.LONG);
        initCreatedTsStateDesc.enableTimeToLive(StateTtlConfig.DISABLED);
        this.createdTsState = getRuntimeContext().getState(initCreatedTsStateDesc);
    }

    @Override
    public void readKey(String key, Context context, Collector<IdentifyNewOperatorKeyedState> out) throws Exception {
        IdentifyNewOperatorKeyedState data = new IdentifyNewOperatorKeyedState();
        data.key = key;
        data.createdTs = createdTsState.value();
        out.collect(data);
    }
}
