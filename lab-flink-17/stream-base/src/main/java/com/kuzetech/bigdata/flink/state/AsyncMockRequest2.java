package com.kuzetech.bigdata.flink.state;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncMockRequest2 extends RichAsyncFunction<Long, Tuple3<Long, Long, Boolean>> {

    @Override
    public void asyncInvoke(Long input, ResultFuture<Tuple3<Long, Long, Boolean>> resultFuture) throws Exception {
        CompletableFuture.supplyAsync(new Supplier<Long>() {
            @Override
            public Long get() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return System.currentTimeMillis();
            }
        }).thenAccept((Long mockResult) -> {
            boolean inState = false;
            if (mockResult - input > 1000) {
                inState = true;
            }
            resultFuture.complete(Collections.singleton(new Tuple3<>(input, mockResult, inState)));
        });
    }
}
