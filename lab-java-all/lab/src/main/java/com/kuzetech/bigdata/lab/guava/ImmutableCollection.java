package com.kuzetech.bigdata.lab.guava;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建不可变集合是我个人最喜欢 Guava 的一个原因，因为创建一个不能删除、不能修改、不能增加元素的集合实在是太实用了。
 * 这样的集合你完全不用担心发生什么问题，总的来说有下面几个优点：
 * <p>
 * 1. 线程安全，因为不能修改任何元素，可以随意多线程使用且没有并发问题。
 * 2. 可以无忧的提供给第三方使用，反正修改不了。
 * 3. 减少内存占用，因为不能改变，所以内部实现可以最大程度节约内存占用。
 * 4. 可以用作常量集合。
 * <p>
 * 特别注意
 * 1. 如果不可变集合的元素是引用对象，那么引用对象的属性是可以更改的
 * 2.
 */
public class ImmutableCollection {
    public static void main(String[] args) {
        ImmutableMap<String, String> immutableMap1 = ImmutableMap.of("key", "value");

        ImmutableMap.Builder<String, String> immutableMapBuilder = ImmutableMap.builder();
        immutableMapBuilder.put("key", "value");
        ImmutableMap<String, String> immutableMap2 = immutableMapBuilder.build();

        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        ImmutableMap<String, String> immutableMap3 = ImmutableMap.copyOf(map);
    }
}
