package com.kuzetech.bigdata.flink.repartition;

import com.kuzetech.bigdata.flink.common.CountParallelSource;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class RescalingApp {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(1000);

        DataStreamSource<Tuple2<String, Long>> source = env.addSource(new CountParallelSource()).setParallelism(2);

        source.rescale().map(new )


        DataStream<String> filtered = source.rescale().filter(new RichFilterFunction<>() {
            @Override
            public boolean filter(Tuple2<String, Long> v) throws Exception {
                RuntimeContext runtimeContext = this.getRuntimeContext();
                System.out.println(runtimeContext.getIndexOfThisSubtask());
                return true;
            }
        }).setParallelism(4);

        filtered.print();

        env.execute();
    }

}
