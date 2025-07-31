package com.kuzetech.bigdata.flink;

import com.kuzetech.bigdata.flink.funny.FunnyMessage;
import com.kuzetech.bigdata.flink.funny.FunnyMessageStatistician;
import org.apache.flink.api.common.functions.AggregateFunction;

public class MessageCountAggregateFunction implements AggregateFunction<FunnyMessage, FunnyMessageStatistician, FunnyMessageStatistician> {


    @Override
    public FunnyMessageStatistician createAccumulator() {
        return new FunnyMessageStatistician();
    }

    @Override
    public FunnyMessageStatistician add(FunnyMessage msg, FunnyMessageStatistician statistician) {
        if (statistician.getApp() == null) {
            statistician.setApp(msg.getApp());
            statistician.setEvent(msg.getEvent());
        }

        if (FunnyMessage.CHANNEL_KEY_KAFKA.equalsIgnoreCase(msg.getChannel())) {
            statistician.incrResult();
        } else {
            statistician.decrResult();
        }

        return statistician;
    }

    @Override
    public FunnyMessageStatistician getResult(FunnyMessageStatistician statistician) {
        return statistician;
    }

    @Override
    public FunnyMessageStatistician merge(FunnyMessageStatistician acc1, FunnyMessageStatistician acc2) {
        acc1.setResult(acc1.getResult() + acc2.getResult());
        return acc1;
    }
}
