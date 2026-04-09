package com.funnydb.mutation.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funnydb.mutation.model.MutationEvent;
import com.funnydb.mutation.service.MutationApplyResult;
import com.funnydb.mutation.service.MutationMetrics;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JedisRedisMutationStore implements RedisMutationStore {
    private final JedisPooled jedis;
    private final ObjectMapper objectMapper;
    private final RedisLuaResultParser resultParser;
    private final String luaScript;
    private final MutationMetrics metrics;

    public JedisRedisMutationStore(JedisPooled jedis, ObjectMapper objectMapper) {
        this(jedis, objectMapper, new MutationMetrics());
    }

    public JedisRedisMutationStore(JedisPooled jedis, ObjectMapper objectMapper, MutationMetrics metrics) {
        this.jedis = jedis;
        this.objectMapper = objectMapper;
        this.resultParser = new RedisLuaResultParser(objectMapper);
        this.luaScript = RedisLuaScriptFactory.mutationScript();
        this.metrics = metrics;
    }

    @Override
    public MutationApplyResult apply(String redisKey, MutationEvent event) {
        long startNanos = System.nanoTime();
        Object rawResult = jedis.eval(
                luaScript,
                Collections.singletonList(redisKey),
                buildArgs(event)
        );
        metrics.recordRedisApply(System.nanoTime() - startNanos);
        return resultParser.parse(rawResult);
    }

    public void close() {
        jedis.close();
    }

    private List<String> buildArgs(MutationEvent event) {
        try {
            return List.of(
                    String.valueOf(event.getData().getTime()),
                    event.getData().getDataLifecycle(),
                    event.getData().getIdentify(),
                    event.getData().getOperate(),
                    objectMapper.writeValueAsString(event.getData().getProperties())
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to serialize redis lua arguments", exception);
        }
    }
}
