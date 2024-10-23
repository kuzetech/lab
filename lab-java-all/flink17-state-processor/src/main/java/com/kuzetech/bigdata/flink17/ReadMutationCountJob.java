package com.kuzetech.bigdata.flink17;

import com.kuzetech.bigdata.flink17.mutation.MutationReaderFunction;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class ReadMutationCountJob {

    public static void main(String[] args) throws Exception {
        String spPath = "/Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/dev-mutation";
        if (args.length > 0) {
            spPath = args[0];
        }

        final int outputCount = args.length > 1 ? Integer.parseInt(args[1]) : 10000;


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                spPath,
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
                            if (t.f1 >= outputCount) {
                                m.remove(e.f0);
                                collector.collect(t);
                            } else {
                                m.put(e.f0, t);
                            }
                        }
                    }

                    @Override
                    public void close() throws Exception {
                        super.close();
                        m.forEach((k, v) -> log.info("未发送到下游的数据: {}", v));
                    }
                });

        result.print();

        env.execute("ReadMutationCountJob");
    }


}
