package com.kuzetech.bigdata.flinkstate.keepalivetoderive;

import com.xmfunny.funnydb.flink.model.ActiveMark;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;

import java.util.concurrent.TimeUnit;

public class DeriveAuBootstrapper extends KeyedStateBootstrapFunction<String, Tuple2<String, ActiveMark>> {

    private ValueState<ActiveMark> activeState;

    private final String activeCycle;

    public DeriveAuBootstrapper(String activeCycle) {
        this.activeCycle = activeCycle;
    }

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<ActiveMark> activeStateDesc = new ValueStateDescriptor<>(String.format("%s-state", activeCycle.toLowerCase()), Types.POJO(ActiveMark.class));
        // 暂定过期时间为60天
        StateTtlConfig ttlCfg = StateTtlConfig.newBuilder(Time.of(60, TimeUnit.DAYS))
                .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp)
                .build();
        activeStateDesc.enableTimeToLive(ttlCfg);
        this.activeState = this.getRuntimeContext().getState(activeStateDesc);
    }

    @Override
    public void processElement(Tuple2<String, ActiveMark> value, Context ctx) throws Exception {
        activeState.update(value.f1);
    }
}