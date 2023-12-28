package com.kuzetech.bigdata.lab.guava;

import com.google.common.base.Splitter;

import java.util.Arrays;

public class StringSplit {
    public static void main(String[] args) {
        String str = ",a,,b,";
        String[] splitArr = str.split(",");
        Arrays.stream(splitArr).forEach(System.out::println);
        System.out.println("------");

        /*
         * JDK 中是自带字符串分割的，我想你也一定用过，那就是 String 的 split 方法，
         * 但是这个方法有一个问题，就是如果最后一个元素为空，那么就会丢弃，奇怪的是第一个元素为空却不会丢弃，这就十分迷惑
         *
         *
         * Guava 提供了 Splitter 类，并且有一系列的操作方式可以直观的控制分割逻辑
         * */

        Iterable<String> split = Splitter.on(",")
                .omitEmptyStrings() // 忽略空值
                .trimResults() // 过滤结果中的空白
                .split(str);
        split.forEach(System.out::println);
    }
}
