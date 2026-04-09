package com.funnydb.mutation.support;

import com.funnydb.mutation.infra.RedisMutationStore;
import com.funnydb.mutation.model.MutationEvent;
import com.funnydb.mutation.service.MutationApplyResult;
import com.funnydb.mutation.service.MutationEngine;
import com.funnydb.mutation.service.MutationEnvelope;
import com.funnydb.mutation.service.MutationRouting;

public class RedisBackedMutationProcessor {
    private final MutationEngine engine;
    private final RedisMutationStore store;

    public RedisBackedMutationProcessor(MutationEngine engine, RedisMutationStore store) {
        this.engine = engine;
        this.store = store;
    }

    public MutationEnvelope process(MutationEvent event, long updatedTime) {
        MutationRouting routing = engine.buildRouting(event);
        MutationApplyResult result = store.apply(routing.getRedisKey(), event);
        return new MutationEnvelope(routing, result, engine.buildSnapshot(event, result.getDocument(), updatedTime));
    }
}
