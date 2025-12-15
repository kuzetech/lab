package com.kuzetech.bigdata.flink.derive.function;

import com.kuzetech.bigdata.flink.derive.domain.IdentifyNewOperatorKeyedState;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;

public class IdentifyNewOperatorKeyedStateBootstrapper extends KeyedStateBootstrapFunction<String, IdentifyNewOperatorKeyedState> {

    private transient ValueState<Long> createdTsState;

    @Override
    public void open(Configuration configuration) throws Exception {
        // 创建时间戳
        ValueStateDescriptor<Long> initCreatedTsStateDesc = new ValueStateDescriptor<>("created-ts-state", Types.LONG);
        initCreatedTsStateDesc.enableTimeToLive(StateTtlConfig.DISABLED);
        this.createdTsState = getRuntimeContext().getState(initCreatedTsStateDesc);
    }

    @Override
    public void processElement(
            IdentifyNewOperatorKeyedState value,
            KeyedStateBootstrapFunction<String, IdentifyNewOperatorKeyedState>.Context context) throws Exception {
        createdTsState.update(value.getCreatedTs());
    }
}
