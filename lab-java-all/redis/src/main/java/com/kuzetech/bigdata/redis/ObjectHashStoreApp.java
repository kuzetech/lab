package com.kuzetech.bigdata.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class ObjectHashStoreApp {

    private static final Logger log = LoggerFactory.getLogger(ObjectHashStoreApp.class);

    public static void main(String[] args) {
        try (
                JedisPool pool = new JedisPool("localhost", 6379);
                Jedis jedis = pool.getResource()
        ) {
            Map<String, String> data = new HashMap<>();
            data.put("#ip_continent", "Asia");
            data.put("#ip_country", "China");
            data.put("#ip_province", "FuJian");
            data.put("#ip_city", "XiaMen");
            data.put("#os_platform", "Android");
            data.put("#device_model", "V2232A");
            data.put("#channel", "unknown");
            data.put("#zone_offset", "8");
            long result = jedis.hset("demo:#device_login:#device_id-47473bd0f17a37c9a0bf95c4823f8aa3", data);
            log.info(String.valueOf(result));

            //used_memory:987712
            //used_memory:1037432
            //used_memory:1037744
            //used_memory:1038056
        }

    }
}
