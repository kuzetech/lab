package com.kuzetech.bigdata.concurrent.atomic;

import java.util.concurrent.atomic.AtomicLong;

public class DemoLong {
    public static void main(String[] args) {
        AtomicLong count = new AtomicLong(0);
        long a = count.getAndIncrement();
        System.out.println(a);

        long b = count.incrementAndGet();
        System.out.println(b);
    }
}
