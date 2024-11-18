package com.kuzetech.bigdata.concurrent.threadpool;

import java.util.concurrent.*;

public class BaseDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 不需要返回值
        Future<?> f1 = executorService.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println(1);
            }
        });
        f1.get();

        // 返回一个新的结果
        Future<Integer> f2 = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 1 + 1;
            }
        });
        System.out.println(f2.get());


        // 返回的结果需要跟外部变量进行交互
        Result r1 = new Result();
        Future<Result> f3 = executorService.submit(new Task(r1), r1);
        Result r2 = f3.get();
        assert r1 == r2; // 返回的是同一个对象
        System.out.println(r2.total);

        executorService.close();
    }

    public static class Task implements Runnable {

        private final Result r;

        public Task(Result r) {
            this.r = r;
        }

        @Override
        public void run() {
            r.total++;
        }
    }

    public static class Result {
        public Integer total = 2;
    }
}
