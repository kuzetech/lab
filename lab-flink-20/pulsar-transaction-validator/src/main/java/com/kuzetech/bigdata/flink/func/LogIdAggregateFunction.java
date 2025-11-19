package com.kuzetech.bigdata.flink.func;

import com.kuzetech.bigdata.flink.funny.FunnyMessage;
import org.apache.flink.api.common.functions.AggregateFunction;

import java.util.HashSet;
import java.util.Set;

public class LogIdAggregateFunction implements AggregateFunction<FunnyMessage, Set<String>, Set<String>> {


    @Override
    public Set<String> createAccumulator() {
        return new HashSet<>();
    }

    @Override
    public Set<String> add(FunnyMessage message, Set<String> acc) {
        if (acc.contains(message.getLogId())) {
            acc.remove(message.getLogId());
        } else {
            acc.add(message.getLogId());
        }
        return acc;
    }

    @Override
    public Set<String> getResult(Set<String> acc) {
        return acc;
    }

    @Override
    public Set<String> merge(Set<String> acc1, Set<String> acc2) {
        Set<String> result = new HashSet<>(acc1);
        Set<String> temp = new HashSet<>(acc2);
        result.removeAll(acc2);
        temp.removeAll(acc1);
        result.addAll(temp);
        return result;
    }
}
