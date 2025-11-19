package com.kuzetech.bigdata.flink.func;

import com.kuzetech.bigdata.flink.funny.FunnyMessage;
import org.apache.flink.api.common.functions.AggregateFunction;

public class MessageCountAggregateFunction implements AggregateFunction<FunnyMessage, Long, Long> {
    
    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(FunnyMessage msg, Long sum) {
        if (FunnyMessage.CHANNEL_KAFKA.equalsIgnoreCase(msg.getChannel())) {
            sum++;
        } else {
            sum--;
        }
        return sum;
    }

    @Override
    public Long getResult(Long statistician) {
        return statistician;
    }

    @Override
    public Long merge(Long acc1, Long acc2) {
        return acc1 + acc2;
    }
}
