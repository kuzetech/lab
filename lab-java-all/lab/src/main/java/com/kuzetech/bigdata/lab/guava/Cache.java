package com.kuzetech.bigdata.lab.guava;


import com.google.common.cache.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 在开发中我们可能需要使用小规模的缓存，来提高访问速度。这时引入专业的缓存中间件可能又觉得浪费
 * Guava 中提供了简单的缓存类，且可以根据预计容量、过期时间等自动过期已经添加的元素。
 * 即使这样我们也要预估好可能占用的内存空间，以防内存占用过多。
 */
public class Cache {

    private static final MyRemovalListener removalListener = new MyRemovalListener();
    private static final CacheLoader cacheLoader;
    private static final LoadingCache loadingCache;

    static {
        cacheLoader = new CacheLoader<String, Animal>() {
            // 如果找不到元素，会调用这里
            @Override
            public Animal load(String name) {
                // 根据 name 从数据库中搜索并加载 Animal 类
                return null;
            }
        };

        loadingCache = CacheBuilder.newBuilder()
                .maximumSize(1000) // 容量
                .expireAfterWrite(3, TimeUnit.SECONDS) // 过期时间
                .removalListener(removalListener) // 失效监听器
                .build(cacheLoader); //
        loadingCache.put("狗", new Animal("旺财", 1));
        loadingCache.put("猫", new Animal("汤姆", 3));
        loadingCache.put("狼", new Animal("灰太狼", 4));
        loadingCache.invalidate("猫"); // 手动失效
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Animal animal = (Animal) loadingCache.get("狼");
        System.out.println(animal);
        Thread.sleep(4 * 1000);
        // 狼已经自动过去，获取为 null 值报错
        System.out.println(loadingCache.get("狼"));
        /**
         * key=猫,value=Animal{name='汤姆', age=3},reason=EXPLICIT
         * Animal{name='灰太狼', age=4}
         * key=狗,value=Animal{name='旺财', age=1},reason=EXPIRED
         * key=狼,value=Animal{name='灰太狼', age=4},reason=EXPIRED
         *
         * Exception in thread "main" com.google.common.cache.CacheLoader$InvalidCacheLoadException: CacheLoader returned null for key 狼.
         */
    }

    /**
     * 缓存移除监听器
     */
    static class MyRemovalListener implements RemovalListener<String, Animal> {

        @Override
        public void onRemoval(RemovalNotification<String, Animal> notification) {
            String reason = String.format("key=%s,value=%s,reason=%s", notification.getKey(), notification.getValue(), notification.getCause());
            System.out.println(reason);
        }
    }

    static class Animal {
        private String name;
        private Integer age;

        @Override
        public String toString() {
            return "Animal{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }

        public Animal(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
