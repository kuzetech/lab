package com.funnydb.mutation.infra;

import com.funnydb.mutation.model.MutationPollBatch;


public interface MutationConsumer {
    MutationPollBatch poll();

    void close();
}
