package com.kuzetech.bigdata.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Hello world!
 */
public class BigStringStoreApp {

    private static final Logger log = LoggerFactory.getLogger(BigStringStoreApp.class);

    public static void main(String[] args) {
        try (
                JedisPool pool = new JedisPool("localhost", 6379);
                Jedis jedis = pool.getResource()
        ) {
            String setResult = jedis.set("demo:#device_login:#device_id-47473bd0f17a37c9a0bf95c4823f8aa1", "{\"#ip_continent\": \"Asia\",\"#ip_country\": \"China\",\"#ip_province\": \"FuJian\",\"#ip_city\": \"XiaMen\",\"#os_platform\": \"Android\",\"#device_model\": \"V2232A\",\"#channel\": \"unknown\",\"#zone_offset\": 8}");
            log.info(setResult);
        }

    }
}
