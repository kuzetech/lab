package com.kuzetech.bigdata.study.redis;

import redis.clients.jedis.Jedis;

import java.util.Map;

public class TestJedis {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("http://127.0.0.1:6379");

        Map<String, String> map = jedis.hgetAll("ttttt");

        if(map == null){
            System.out.println("我是空的");
        }else if(map.size() == 0){
            System.out.println("我没有元素");
        }else{
            System.out.println("我有值");
        }

    }
}
