package com.kuzetech.bigdata.lab.guava;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

public class Validator {
    public static void main(String[] args) {
        // 检查空值
        String param = "null";
        String name = Preconditions.checkNotNull(param, "param is null");
        // java.lang.NullPointerException: param is null

        // 预期值判断
        String input = "aaaa";
        String wanted = "aaaa";
        Preconditions.checkArgument(wanted.equals(input), "[%s] 404 NOT FOUND", input);
        // java.lang.IllegalArgumentException: [www.wdbyte.com2] 404 NOT FOUND

        // 检查状态
        boolean status = true;
        Preconditions.checkState(status, "status is illegal");
        // java.lang.IllegalStateException: status is illegal

        // 检查数组或集合的元素获取是否越界
        List<String> nameList = Lists.newArrayList("a", "b", "c", "d");
        int index = Preconditions.checkElementIndex(5, nameList.size(), "nameList");
        // java.lang.IndexOutOfBoundsException: nameList (5) must be less than size (4)

    }
}
