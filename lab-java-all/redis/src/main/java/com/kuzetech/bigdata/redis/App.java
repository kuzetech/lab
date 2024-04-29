package com.kuzetech.bigdata.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Hello world!
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try (
                JedisPool pool = new JedisPool("localhost", 6379);
                Jedis jedis = pool.getResource()
        ) {
            String setResult = jedis.set("first", "1");
            log.info(setResult); // OK
            String value = jedis.get("first");
            log.info(value);
        }

    }
}
