package com.kuzetech.bigdata.concurrent.completablefuture;

import java.util.concurrent.CompletableFuture;

public class HandleDemo {
    public static void main(String[] args) {
        CompletableFuture<Integer> f0 = CompletableFuture
                .supplyAsync(() -> (7 / 0))
                .thenApply(r -> r * 10)
                .exceptionally(e -> 2)
                .handle((r, e) -> r * 2);

        // 结果返回 4
        System.out.println(f0.join());
    }
}
