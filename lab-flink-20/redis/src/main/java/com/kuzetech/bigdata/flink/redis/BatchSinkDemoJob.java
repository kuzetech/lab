package com.kuzetech.bigdata.flink.redis;

import com.kuzetech.bigdata.flink.base.FlinkUtil;
import com.kuzetech.bigdata.flink.redis.sink.RedisBatchSink;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class BatchSinkDemoJob {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = FlinkUtil.initEnv(ParameterTool.fromArgs(args));
        env.setParallelism(1);

        List<String> collectionList = new ArrayList<>();
        collectionList.add("a");
        collectionList.add("b");
        collectionList.add("c");

        DataStreamSource<String> source = env.fromCollection(collectionList);

        SingleOutputStreamOperator<Tuple2<String, String>> redisTupleStream = source.map(v -> Tuple2.of(v, "test"));

        RedisBatchSink redisPipelineSink = new RedisBatchSink();

        redisTupleStream.addSink(redisPipelineSink);

        env.execute();
    }
}


