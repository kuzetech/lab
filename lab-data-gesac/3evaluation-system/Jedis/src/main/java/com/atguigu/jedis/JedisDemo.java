package com.atguigu.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author WEIYUNHUI
 * @date 2024/9/6 14:02
 */
public class JedisDemo {

    /**
     * 作业:  测试五大数据类型的API方法
     */
    public static void testString(){}
    public static void testList(){}
    public static void testSet(){}
    public static void testZset(){}
    public static void testHash(){}


    public static final String host = "hadoop102" ;
    public static final int port = 6379 ;

    //连接池对象
    public  static JedisPool jedisPool = null ;

    static{
        //连接池的主要配置
        JedisPoolConfig jedisPoolConfig =new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10); //最大可用连接数
        jedisPoolConfig.setMaxIdle(5); //最大闲置连接数
        jedisPoolConfig.setMinIdle(5); //最小闲置连接数
        jedisPoolConfig.setBlockWhenExhausted(true); //连接耗尽是否等待
        jedisPoolConfig.setMaxWaitMillis(2000); //等待时间
        jedisPoolConfig.setTestOnBorrow(true); //取连接的时候进行一下测试 ping pong

        jedisPool = new JedisPool(jedisPoolConfig , host , port ) ;
    }

    /**
     * 连接池的方式获取Jedis对象
     * @return
     */
    public static Jedis getJedisFromPool(){
        Jedis jedis = jedisPool.getResource();
        return jedis ;
    }


    /**
     * new 的方式 ， 获取Jedis对象
     */
    public static Jedis getJedis(){

        Jedis jedis = new Jedis(host , port);

        return jedis ;
    }

    public static void main(String[] args) {
        //Jedis jedis = getJedis();
        Jedis jedis = getJedisFromPool();

        // 五大数据类型的命令 与 Jedis的 API方法一一对应
        String pong = jedis.ping();
        System.out.println(pong);

        jedis.set("name" , "zhangsan") ;

        String name = jedis.get("name");

        System.out.println(name);

        //关闭/归还对象
        jedis.close();
    }



}
