package com.kuzetech.bigdata.flink.utils;

import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class RedisUtil {

    public static JedisPool jedisPool = null;

    public static JedisPoolConfig jedisPoolConfig;

    //确保拿到的jedis连接是唯一的，从而完成事务 不加入序列化
    @Getter
    private final transient Jedis jedis;

    private transient Transaction jedisTransaction;

    //JedisPool配置类提前加载
    static {
        jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(100); //最大可用连接数
        jedisPoolConfig.setBlockWhenExhausted(true); //连接耗尽是否等待
        jedisPoolConfig.setMaxWait(Duration.ofMillis(2000)); //等待时间
        jedisPoolConfig.setMaxIdle(5); //最大闲置连接数
        jedisPoolConfig.setMinIdle(5); //最小闲置连接数
        jedisPoolConfig.setTestOnBorrow(false); //取连接的时候进行一下测试 pingpong
    }

    //无参构造
    public RedisUtil() throws IOException {
        InputStream in = RedisUtil.class.getClassLoader().getResourceAsStream("redis.properties");
        Properties properties = new Properties();
        properties.load(in);
        String port = properties.getProperty("redis.port");
        String timeout = properties.getProperty("redis.timeout");
        jedisPool = new JedisPool(jedisPoolConfig, properties.getProperty("redis.host"), Integer.parseInt(port), Integer.parseInt(timeout));
        //System.out.println("开辟连接池");
        jedis = jedisPool.getResource();
        jedis.auth("root");
    }

    //获取jedis
    public Transaction getTransaction() {
        if (this.jedisTransaction == null) {
            jedisTransaction = this.jedis.multi();
            System.out.println("========" + jedisTransaction);
            System.out.println(jedisTransaction);
        }
        return this.jedisTransaction;
    }

    public void setJedisTransactionIsNull() {
        this.jedisTransaction = null;
    }
}