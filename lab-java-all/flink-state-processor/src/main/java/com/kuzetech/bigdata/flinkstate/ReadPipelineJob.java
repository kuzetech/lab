package com.kuzetech.bigdata.flinkstate;

import com.kuzetech.bigdata.flinkstate.tracktopipeline.PipelineDistinctStateReaderFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.state.api.OperatorIdentifier;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;

public class ReadPipelineJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SavepointReader savepoint = SavepointReader.read(
                env,
                "/Users/huangsw/code/lab/lab-java-all/flink-state-processor/savepoints/new",
                new EmbeddedRocksDBStateBackend(true));

        DataStream<Tuple2<String, Boolean>> distinctState = savepoint.readKeyedState(
                OperatorIdentifier.forUid("filter-distinct"),
                new PipelineDistinctStateReaderFunction());

        SingleOutputStreamOperator<Integer> sum = distinctState.map(new MapFunction<Tuple2<String, Boolean>, Integer>() {
                    @Override
                    public Integer map(Tuple2<String, Boolean> stringBooleanTuple2) throws Exception {
                        return 1;
                    }
                }).windowAll(GlobalWindows.create())
                .trigger(CountTrigger.of(50000))
                .sum(0);

        sum.print();

        env.execute("ReadPipelineJob");
    }
}
