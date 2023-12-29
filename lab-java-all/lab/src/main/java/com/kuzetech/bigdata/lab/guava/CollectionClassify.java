package com.kuzetech.bigdata.lab.guava;

import com.google.common.collect.HashMultimap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CollectionClassify {
    public static void main(String[] args) {
        // JDK 原生写法
        HashMap<String, Set<String>> animalMap = new HashMap<>();
        HashSet<String> dogSet = new HashSet<>();
        dogSet.add("旺财");
        dogSet.add("大黄");
        animalMap.put("狗", dogSet);
        HashSet<String> catSet = new HashSet<>();
        catSet.add("加菲");
        catSet.add("汤姆");
        animalMap.put("猫", catSet);
        System.out.println(animalMap.get("猫")); // [加菲, 汤姆]

        // use guava
        HashMultimap<String, String> multimap = HashMultimap.create();
        multimap.put("狗", "大黄");
        multimap.put("狗", "旺财");
        multimap.put("猫", "加菲");
        multimap.put("猫", "汤姆");
        multimap.put("猫", "汤姆");
        System.out.println(multimap); // {狗=[旺财, 大黄], 猫=[加菲, 汤姆]}
        System.out.println(multimap.get("猫")); // [加菲, 汤姆]
    }
}
