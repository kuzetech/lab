package com.kuzetech.bigdata.flink.study;

import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

public class RuleEvaluator
        extends BroadcastProcessFunction<Event, Rule, Tuple2<String, Event>> {

    MapStateDescriptor<Void, Rule> ruleDesc;

    @Override
    public void open(Configuration conf) {
        ruleDesc = new MapStateDescriptor<>("rules", Types.VOID, Types.POJO(Rule.class));
    }

    /**
     * Called for each user action.
     * Evaluates the current pattern against the previous and
     * current action of the user.
     */
    @Override
    public void processElement(
            Event event,
            ReadOnlyContext ctx,
            Collector<Tuple2<String, Event>> out) throws Exception {
        // get current pattern from broadcast state
        Rule rule = ctx
                .getBroadcastState(this.ruleDesc)
                // access MapState with null as VOID default value
                .get(null);
        out.collect(new Tuple2<>(rule.getOutput(), event));
    }

    /**
     * Called for each new pattern.
     * Overwrites the current pattern with the new pattern.
     */
    @Override
    public void processBroadcastElement(
            Rule rule,
            Context ctx,
            Collector<Tuple2<String, Event>> out) throws Exception {
        // store the new pattern by updating the broadcast state
        BroadcastState<Void, Rule> bcState = ctx.getBroadcastState(this.ruleDesc);
        // storing in MapState with null as VOID default value
        bcState.put(null, rule);
    }
}
