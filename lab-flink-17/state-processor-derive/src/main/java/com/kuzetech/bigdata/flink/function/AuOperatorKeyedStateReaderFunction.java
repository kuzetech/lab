package com.kuzetech.bigdata.flink.function;

import com.kuzetech.bigdata.flink.domain.AuOperatorKeyedState;
import com.xmfunny.funnydb.flink.model.ActiveMark;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

public class AuOperatorKeyedStateReaderFunction extends KeyedStateReaderFunction<String, AuOperatorKeyedState> {

    private final String stateName;
    private transient ValueState<ActiveMark> activeState;

    public AuOperatorKeyedStateReaderFunction(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public void open(Configuration configuration) throws Exception {
        ValueStateDescriptor<ActiveMark> activeStateDesc = new ValueStateDescriptor<>(stateName, Types.POJO(ActiveMark.class));
        // 原本的过期时间为60天
        StateTtlConfig ttlCfg = StateTtlConfig.newBuilder(Time.days(1000))
                .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp)
                .build();
        activeStateDesc.enableTimeToLive(ttlCfg);
        this.activeState = this.getRuntimeContext().getState(activeStateDesc);
    }

    @Override
    public void readKey(String key, Context context, Collector<AuOperatorKeyedState> out) throws Exception {
        if (key != null && !key.startsWith("demo")) {
            AuOperatorKeyedState data = new AuOperatorKeyedState();
            data.key = key;
            data.activeMark = activeState.value();
            out.collect(data);
        }
    }
}
