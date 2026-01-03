package com.kuzetech.bigdata.flink.redis.func;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.time.Duration;

@Slf4j
public class StateProcessFunc extends ProcessFunction<String, String> implements CheckpointedFunction {

    private static JedisPool jedisPool;
    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(100); //最大可用连接数
        jedisPoolConfig.setBlockWhenExhausted(true); //连接耗尽是否等待
        jedisPoolConfig.setMaxWait(Duration.ofMillis(2000)); //等待时间
        jedisPoolConfig.setMaxIdle(5); //最大闲置连接数
        jedisPoolConfig.setMinIdle(5); //最小闲置连接数
        jedisPoolConfig.setTestOnBorrow(false); //取连接的时候进行一下测试 pingpong
        jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379, 5000);
    }

    private transient Jedis conn;
    private transient Transaction jedisTransaction;

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        log.info("StateProcessFunc initializeState first");
    }

    @Override
    public void open(OpenContext openContext) throws Exception {
        log.info("StateProcessFunc open second");
        conn = jedisPool.getResource();
    }

    @Override
    public void processElement(String value, ProcessFunction<String, String>.Context ctx, Collector<String> out) throws Exception {
        if (jedisTransaction == null) {
            jedisTransaction = conn.multi();
        }
        jedisTransaction.set(value, String.valueOf(System.currentTimeMillis()));
        out.collect(value);
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        log.info("StateProcessFunc snapshotState");
        jedisTransaction.exec();
        jedisTransaction=null;
    }

    @Override
    public void close() throws Exception {
        log.info("StateProcessFunc close");
        jedisTransaction.discard();
        conn.close();
    }
}
