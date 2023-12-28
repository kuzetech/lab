package com.kuzetech.bigdata.lab.guava;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class StringJoin {
    public static void main(String[] args) {
        // JDK 方式一
        List<String> list = Lists.newArrayList("a", "b", "c", null);
        String join = String.join(",", list);
        System.out.println(join); // a,b,c,null
        // JDK 方式二
        String result = list.stream().collect(Collectors.joining(","));
        System.out.println(result); // a,b,c,null
        // JDK 方式三
        StringJoiner stringJoiner = new StringJoiner(",");
        list.forEach(stringJoiner::add);
        System.out.println(stringJoiner.toString()); // a,b,c,null


        /*
         * JDK 8 中其实已经内置了字符串拼接方法，但是它只是简单的拼接，没有额外操作，比如过滤掉 null 元素，去除前后空格等
         * 可以看到 null 值也被拼接到了字符串里，这有时候不是我们想要的
         *
         *  可以看到使用 skipNulls() 可以跳过空值，使用 useFornull(String) 可以为空值自定义显示文本
         *
         * */

        // guava
        String join2 = Joiner.on(",").skipNulls().join(list);
        System.out.println(join2); // a,b,c

        String join1 = Joiner.on(",").useForNull("空值").join("旺财", "汤姆", "杰瑞", null);
        System.out.println(join1); // 旺财,汤姆,杰瑞,空值
    }
}
