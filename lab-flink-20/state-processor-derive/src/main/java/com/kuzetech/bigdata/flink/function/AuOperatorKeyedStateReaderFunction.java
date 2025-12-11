package com.kuzetech.bigdata.flink.function;

import com.kuzetech.bigdata.flink.domain.AuOperatorKeyedState;
import com.xmfunny.funnydb.flink.model.ActiveMark;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;

@Slf4j
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
        StateTtlConfig ttlCfg = StateTtlConfig.newBuilder(Duration.ofDays(1000))
                .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp)
                .build();
        activeStateDesc.enableTimeToLive(ttlCfg);
        this.activeState = this.getRuntimeContext().getState(activeStateDesc);
    }

    /*
        不能直接在这里过滤数据，如果startsWith demo 直接 return 将不返回任何数据
        如果需要实现过滤，并且加速作业的话，这里一样得用 out 输出数据，然后外层通过 filter 进行过滤
     */
    @Override
    public void readKey(String key, Context context, Collector<AuOperatorKeyedState> out) throws Exception {
        AuOperatorKeyedState data = new AuOperatorKeyedState();
        data.key = key;
        if (!key.startsWith("demo")) {
            data.activeMark = activeState.value();
        }
        out.collect(data);
    }
}
