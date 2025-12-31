package com.kuzetech.bigdata.flink.redis;

import com.kuzetech.bigdata.flink.model.WordCount;
import com.kuzetech.bigdata.flink.utils.FlinkUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class TwoPhaseCommitRedisSinkDemo {
    public static void main(String[] args) throws Exception {
        String jobName = "TwoPhaseCommitRedisSinkDemo";

        //1.获取流式执行环境
        StreamExecutionEnvironment env = FlinkUtil.getEnvironment(jobName);

        //2.设置 jedis 的序列化
        // env.getConfig().addDefaultKryoSerializer(Jedis.class, TBaseSerializer.class);

        List<String> collectionList = new ArrayList<>();
        collectionList.add("a");
        collectionList.add("b");
        collectionList.add("c");

        DataStreamSource<String> source = env.fromCollection(collectionList);

        //5.map包装数据为value,1
        SingleOutputStreamOperator<Tuple2<String, Integer>> mapStream = source.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String value) throws Exception {
                return new Tuple2<>(value, 1);
            }
        });

        //6.mapStream进行keyby并且聚合
        SingleOutputStreamOperator<Tuple2<String, Integer>> reduceStream = mapStream.keyBy(data -> data.f0)
                .reduce(new ReduceFunction<Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                        return new Tuple2<>(value1.f0, value1.f1 + value2.f1);
                    }
                });

        //7.reduceStream包装成POJO类
        SingleOutputStreamOperator<WordCount> pojoStream = reduceStream.map(data -> {
            return new WordCount(data.f0, data.f1);
        });

        //8.pojoStream输出到redis，这里以Hash表的形式类似
        // WordAndWordCount java 1 python 1
        pojoStream.addSink(new RedisExactlySink<WordCount>());

        //9.任务执行
        env.execute();
    }
}
