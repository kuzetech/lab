package com.kuzetech.bigdata.concurrent.virtualthread;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class BaseDemoReturnResult {
    public static void main(String[] args) {

        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                //int test = 1 / 0;
                Thread.sleep(1000);
                return 3;
            }
        });

        Thread t1 = Thread.startVirtualThread(futureTask);
        try {
            System.out.println(new Date().getTime());
            Integer result = futureTask.get();
            System.out.println(new Date().getTime());
            System.out.println(result);
        } catch (RuntimeException e) {
            // 按需处理
            System.out.println("RuntimeException");
        } catch (Throwable e) {
            // 按需处理
            System.out.println("Throwable");
        }


    }
}
