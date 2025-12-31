package com.kuzetech.bigdata.flink.redis.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.time.Duration;

@Slf4j
public class RedisUtil {

    public static JedisPool jedisPool;

    //确保拿到的jedis连接是唯一的，从而完成事务 不加入序列化
    @Getter
    private final transient Jedis jedis;

    private transient Transaction jedisTransaction;

    //JedisPool配置类提前加载
    public static JedisPoolConfig jedisPoolConfig;

    static {
        jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(100); //最大可用连接数
        jedisPoolConfig.setBlockWhenExhausted(true); //连接耗尽是否等待
        jedisPoolConfig.setMaxWait(Duration.ofMillis(2000)); //等待时间
        jedisPoolConfig.setMaxIdle(5); //最大闲置连接数
        jedisPoolConfig.setMinIdle(5); //最小闲置连接数
        jedisPoolConfig.setTestOnBorrow(false); //取连接的时候进行一下测试 pingpong
    }

    public RedisUtil() throws IOException {
        jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379, 5000);
        jedis = jedisPool.getResource();
        jedis.auth("root");
    }

    public Transaction getTransaction() {
        if (this.jedisTransaction == null) {
            jedisTransaction = this.jedis.multi();
        }
        return this.jedisTransaction;
    }

    public void setJedisTransactionIsNull() {
        this.jedisTransaction = null;
    }
}