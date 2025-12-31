package com.kuzetech.bigdata.flink.redis.sink;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.HashMap;

public class RedisBatchSink extends RichSinkFunction<Tuple2<String, String>> {

    private final static Integer MAX_BATCH_SIZE = 2;

    private JedisPool pool;
    private HashMap<String, String> batch;

    @Override
    public void open(Configuration parameters) throws Exception {
        pool = new JedisPool("localhost", 6379);
        batch = new HashMap<>();
    }

    @Override
    public void invoke(Tuple2<String, String> value, Context context) throws Exception {
        batch.put(value.f0, value.f1);
        if (batch.size() >= MAX_BATCH_SIZE) {
            executeBatchInsert();
            batch.clear();
        }
    }

    @Override
    public void close() throws Exception {
        if (!batch.isEmpty()) {
            executeBatchInsert();
        }
        if (pool != null) {
            pool.close();
        }
    }

    private void executeBatchInsert() {
        try (Jedis client = pool.getResource()) {
            Pipeline pipelined = client.pipelined();
            batch.forEach(pipelined::set);
            pipelined.sync();
        }
    }
}
