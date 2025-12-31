package com.kuzetech.bigdata.flink.redis;

import com.kuzetech.bigdata.flink.base.FlinkUtil;
import com.kuzetech.bigdata.flink.redis.sink.RedisEosSink;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class TwoPhaseCommitSinkDemoJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = FlinkUtil.initEnv(ParameterTool.fromArgs(args));

        List<String> collectionList = new ArrayList<>();
        collectionList.add("a");
        collectionList.add("b");
        collectionList.add("c");

        env.fromCollection(collectionList)
                .map(v -> Tuple2.of(v, 1))
                .addSink(new RedisEosSink<Tuple2<String, Integer>>());

        env.execute();
    }
}
