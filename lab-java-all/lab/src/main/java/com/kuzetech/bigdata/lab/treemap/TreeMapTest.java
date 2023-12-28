package com.kuzetech.bigdata.lab.treemap;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

public class TreeMapTest {

    public static void main(String[] args) {

        // key 的类型必须实现 Comparable 接口
        // 默认从小到大
        TreeMap<Long, String> tm = new TreeMap<>();
        tm.put(1L, "1");
        tm.put(10L, "10");
        tm.put(50L, "50");

        // 找到 >= key 的子节点树
        SortedMap<Long, String> childTree = tm.tailMap(20L);
        Long firstKey = childTree.firstKey();
        String result = childTree.get(firstKey);
        System.out.println(result); // 结果 = 50


        // 重新实现 Comparator 接口，实现 从大到小的排序
        TreeMap<Long, String> tm2 = new TreeMap<>(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                if (o1 < o2) {
                    return 1;
                }
                if (o1 > o2) {
                    return -1;
                }
                return 0;
            }
        });

        tm2.put(1L, "1");
        tm2.put(10L, "10");
        tm2.put(50L, "50");

        SortedMap<Long, String> childTree2 = tm2.tailMap(20L);
        Long firstKey2 = childTree2.firstKey();
        String result2 = childTree2.get(firstKey2);
        System.out.println(result2); // 结果 = 10

    }

}
