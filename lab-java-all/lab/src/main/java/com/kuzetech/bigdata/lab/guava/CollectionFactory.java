package com.kuzetech.bigdata.lab.guava;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 可以在创建时直接扔进去几个元素，这个简直太赞了，再也不用一个个 add 了
 */
public class CollectionFactory {
    public static void main(String[] args) {
        // 创建一个 ArrayList 集合
        List<String> list1 = Lists.newArrayList();
        // 创建一个 ArrayList 集合，同时塞入3个数据
        List<String> list2 = Lists.newArrayList("a", "b", "c");
        // 创建一个 ArrayList 集合，容量初始化为10
        List<String> list3 = Lists.newArrayListWithCapacity(10);

        List<String> linkedList1 = Lists.newLinkedList();
        List<String> cowArrayList = Lists.newCopyOnWriteArrayList(); // 线程安全的 list

        HashSet<Object> hashSet = Sets.newHashSet();
        HashSet<String> newHashSet = Sets.newHashSet("a", "a", "b", "c");

        HashMap<Object, Object> hashMap = Maps.newHashMap();
        ConcurrentMap<Object, Object> concurrentMap = Maps.newConcurrentMap(); // 线程安全的 map
        TreeMap<Long, String> treeMap = Maps.newTreeMap(); // key 的类型必须实现 comparable 接口


    }
}
