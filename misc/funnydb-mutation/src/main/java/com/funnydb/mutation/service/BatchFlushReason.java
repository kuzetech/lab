package com.funnydb.mutation.service;

public enum BatchFlushReason {
    BATCH_SIZE_REACHED,
    FLUSH_INTERVAL_REACHED,
    PARTITION_REVOKED,
    SHUTDOWN
}
