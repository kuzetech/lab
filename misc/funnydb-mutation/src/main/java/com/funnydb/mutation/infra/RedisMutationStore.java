package com.funnydb.mutation.infra;

import com.funnydb.mutation.model.MutationEvent;
import com.funnydb.mutation.service.MutationApplyResult;

public interface RedisMutationStore {
    MutationApplyResult apply(String redisKey, MutationEvent event);
}
