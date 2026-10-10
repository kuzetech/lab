package com.atguigu.dga.governance.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author WEIYUNHUI
 * @date 2024/9/3 15:36
 */
public class CompletableFutureTest {
    public static void main(String[] args) {

        bingxing();
    }

    public static void bingxing(){

        //线程池
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(6, 6 , 60 , TimeUnit.SECONDS ,  new LinkedBlockingDeque<>(3)) ;

        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 6);

        long start = System.currentTimeMillis();

        //对nums中的每个元素求平方， 最后求和
        //求平方
        ArrayList<CompletableFuture<Integer>> futures = new ArrayList<>(nums.size());
        for (Integer num : nums) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(
                    () -> {
                        // 模拟耗时
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        //任务
                        return num * num;
                    }
                    ,
                    executor
            );

            futures.add(future) ;
        }

        //执行异步任务， 集结结果
        List<Integer> squareList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        //求和
        Integer sum = 0 ;
        for (Integer num : squareList) {
            sum += num ;
        }

        System.out.println("耗时: " + (System.currentTimeMillis() - start) );

        System.out.println("和为: " + sum );
    }

    public static void chuanxing(){
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 6);

        long start = System.currentTimeMillis();

        //对nums中的每个元素求平方， 最后求和
        //求平方
        ArrayList<Integer> squareList = new ArrayList<>();
        for (Integer num : nums) {
            squareList.add( num * num ) ;
            // 模拟耗时
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //求和
        Integer sum = 0 ;
        for (Integer num : squareList) {
            sum += num ;
        }

        System.out.println("耗时: " + (System.currentTimeMillis() - start) );

        System.out.println("和为: " + sum );
    }
}
