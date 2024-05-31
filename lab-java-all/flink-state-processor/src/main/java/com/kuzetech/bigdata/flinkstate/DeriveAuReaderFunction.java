package com.kuzetech.bigdata.flinkstate;

import com.xmfunny.funnydb.flink.model.ActiveMark;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

@Slf4j
public class DeriveAuReaderFunction extends KeyedStateReaderFunction<String, Tuple2<String, ActiveMark>> {

    private ValueState<ActiveMark> activeState;

    private final String activeCycle;

    public DeriveAuReaderFunction(String activeCycle) {
        this.activeCycle = activeCycle;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
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
    public void readKey(String key, Context ctx, Collector<Tuple2<String, ActiveMark>> out) throws Exception {
        log.debug("key = {},", key);
        out.collect(Tuple2.of(key, activeState.value()));
    }
}
