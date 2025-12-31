package com.kuzetech.bigdata.flink.redis;

import com.kuzetech.bigdata.flink.utils.FlinkUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PipelineRedisSinkDemo {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = FlinkUtil.getEnvironment("PipelineRedisSinkDemo");
        env.setParallelism(1);

        List<String> collectionList = new ArrayList<>();
        collectionList.add("a");
        collectionList.add("b");
        collectionList.add("c");

        DataStreamSource<String> source = env.fromCollection(collectionList);

        SingleOutputStreamOperator<Tuple2<String, String>> redisTupleStream = source.map(new MapFunction<>() {
            private final Random random = new Random();

            @Override
            public Tuple2<String, String> map(String value) throws Exception {
                return new Tuple2<>(String.valueOf(random.nextInt(1000)), value);
            }
        });

        RedisPipelineSink redisPipelineSink = new RedisPipelineSink();

        redisTupleStream.addSink(redisPipelineSink);

        env.execute("PipelineRedisSinkDemo");
    }
}


