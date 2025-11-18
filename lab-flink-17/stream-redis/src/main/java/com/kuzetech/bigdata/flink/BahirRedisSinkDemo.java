package com.kuzetech.bigdata.flink;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;

public class BahirRedisSinkDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<String> lines = env.socketTextStream("node01", 9999);

        SingleOutputStreamOperator<Tuple2<String, Integer>> wordAndOne = lines.flatMap((line, collector) -> {
            String[] words = line.split(" ");
            for (String word : words) {
                collector.collect(Tuple2.of(word, 1));
            }
        });

        KeyedStream<Tuple2<String, Integer>, String> keyed = wordAndOne.keyBy(tp -> tp.f0);
        SingleOutputStreamOperator<Tuple2<String, Integer>> summed = keyed.sum(1);

        //调用Sink
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder()
                .setHost("node03")
                .setPassword("123456")
                .setDatabase(8)
                .build();
        summed.addSink(new RedisSink<>(conf, new RedisWordCountMapper()));

        //启动执行
        env.execute("StreamingWordCount");
    }

    public static class RedisWordCountMapper implements RedisMapper<Tuple2<String, Integer>> {

        @Override
        public RedisCommandDescription getCommandDescription() {
            return new RedisCommandDescription(RedisCommand.HSET, "WORD_COUNT");
        }

        @Override
        public String getKeyFromData(Tuple2<String, Integer> data) {
            return data.f0;
        }

        @Override
        public String getValueFromData(Tuple2<String, Integer> data) {
            return data.f1.toString();
        }
    }
}
