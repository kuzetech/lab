package com.kuzetech.bigdata.concurrent.virtualthread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class BaseDemo {
    public static void main(String[] args) {

        // 直接创建虚拟线程并运行
        Thread t1 = Thread.startVirtualThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(1);
            }
        });

        // 创建虚拟线程但不自动运行，而是手动调用start()开始运行
        Thread t2 = Thread.ofVirtual().unstarted(() -> {
            System.out.println(2);
        });
        t2.start();

        // 通过虚拟线程的ThreadFactory创建虚拟线程，然后手动调用start()开始运行
        ThreadFactory tf = Thread.ofVirtual().factory();
        Thread t3 = tf.newThread(() -> {
            System.out.println(3);
        });
        t3.start();

        // 直接调用 start() 实际上是由ForkJoinPool的线程来调度的。我们也可以自己创建调度线程，然后运行虚拟线程

        // 创建虚拟线程池
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                System.out.println(4);
            });
        }
    }
}
