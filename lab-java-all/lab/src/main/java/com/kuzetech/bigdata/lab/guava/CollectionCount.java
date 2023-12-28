package com.kuzetech.bigdata.lab.guava;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionCount {
    public static void main(String[] args) {
        // Java 统计相同元素出现的次数
        List<String> words = Lists.newArrayList("a", "b", "c", "d", "a", "c");
        Map<String, Integer> countMap = new HashMap<>();
        for (String word : words) {
            Integer count = countMap.get(word);
            count = (count == null) ? 1 : ++count;
            countMap.put(word, count);
        }
        countMap.forEach((k, v) -> System.out.println(k + ":" + v));

        // guava 统计相同元素出现的次数
        HashMultiset<String> multiset = HashMultiset.create(words);
        multiset.elementSet().forEach(s -> System.out.println(s + ":" + multiset.count(s)));
    }
}
