package com.kuzetech.bigdata.lab.guava;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.util.List;

public class BloomFilterTest {

    public static void main(String[] args) {
        // 过滤器中存储数据
        int total = 1000000;
        List<String> baseList = Lists.newArrayList();
        for (int i = 0; i < total; i++) {
            baseList.add("" + i);
        }

        // 待匹配的数据
        // 布隆过滤器应该匹配到的数据量：1000000
        List<String> inputList = Lists.newArrayList();
        for (int i = 0; i < total + 10000; i++) {
            inputList.add("" + i);
        }

        // （1）条件：expectedInsertions：100w，fpp：3%
        // 结果：numBits：约729w（数组size:约729w/64），numHashFunctions：5
        System.out.println("expectedInsertions：100w，fpp：3%，匹配到的数量 " + bloomFilterCheck(baseList, inputList, baseList.size(), 0.03));

        // （2）条件：expectedInsertions：100w，fpp：0.0001%
        // 结果：numBits：约2875w（数组size:约2875w/64），numHashFunctions：20
        System.out.println("expectedInsertions：100w，fpp：0.0001%，匹配到的数量 " + bloomFilterCheck(baseList, inputList, baseList.size(), 0.000001));

        // （3）条件：expectedInsertions：1000w，fpp：3%
        // 结果：numBits：约7298w（数组size:约7298w/64），numHashFunctions：5
        System.out.println("expectedInsertions：1000w，fpp：3%，匹配到的数量 " + bloomFilterCheck(baseList, inputList, baseList.size() * 10, 0.03));
    }

    /**
     * 布隆过滤器
     *
     * @param baseList           过滤器中存储的数据
     * @param inputList          待匹配的数据
     * @param expectedInsertions 可能插入到过滤器中的总数据量
     * @param fpp                误判率
     * @return 在布隆过滤器中可能存在数据量
     */
    private static int bloomFilterCheck(List<String> baseList, List<String> inputList, int expectedInsertions, double fpp) {
        // 创建布隆过滤器
        BloomFilter<String> bf = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), expectedInsertions, fpp);
        // 初始化数据到过滤器中
        for (String string : baseList) {
            // 在对应索引位置存储非0的hash值
            bf.put(string);
        }

        // 判断值是否存在过滤器中
        int count = 0;
        for (String string : inputList) {
            if (bf.mightContain(string)) {
                count++;
            }
        }

        return count;
    }

}
