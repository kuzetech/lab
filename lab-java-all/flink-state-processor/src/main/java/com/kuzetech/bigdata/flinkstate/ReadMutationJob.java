package com.kuzetech.bigdata.flinkstate;

import com.kuzetech.bigdata.flinkstate.mutation.MutationReaderFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

public class ReadMutationJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                "/Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/dev-mutation",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<Tuple2<String, Integer>> mutationState = savepoint.readKeyedState(
                OperatorIdentifier.forUid("mutation-state-process"),
                new MutationReaderFunction());


        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> result = mutationState
                .map(o -> Tuple3.of(o.f0, 1, o.f1), Types.TUPLE(Types.STRING, Types.INT, Types.INT))
                .process(new ProcessFunction<>() {
                    private final Map<String, Tuple3<String, Integer, Integer>> m = new HashMap<>();

                    @Override
                    public void processElement(
                            Tuple3<String, Integer, Integer> e,
                            ProcessFunction<Tuple3<String, Integer, Integer>, Tuple3<String, Integer, Integer>>.Context context,
                            Collector<Tuple3<String, Integer, Integer>> collector) throws Exception {
                        Tuple3<String, Integer, Integer> t = m.get(e.f0);
                        if (t == null) {
                            m.put(e.f0, e);
                        } else {
                            t.f1 += e.f1;
                            t.f2 += e.f2;
                            if (t.f1 >= 5000) {
                                m.remove(e.f0);
                                collector.collect(t);
                            } else {
                                m.put(e.f0, t);
                            }
                        }

                    }
                });


        result.print();

        env.execute("ReadMutationJob");
    }


}
