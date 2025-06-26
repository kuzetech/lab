package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.base.CommonSourceMessage;
import org.apache.flink.api.common.functions.AggregateFunction;

public class MessageCountAggregateFunction implements AggregateFunction<CommonSourceMessage, Long, Long> {


    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(CommonSourceMessage commonSourceMessage, Long aLong) {
        if (CommonSourceMessage.SOURCE_KEY_KAFKA.equalsIgnoreCase(commonSourceMessage.getSource())) {
            return aLong + 1;
        }
        return aLong - 1;
    }

    @Override
    public Long getResult(Long aLong) {
        return aLong;
    }

    @Override
    public Long merge(Long aLong, Long acc1) {
        return aLong + acc1;
    }
}
