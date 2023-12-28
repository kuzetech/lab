package com.kuzetech.bigdata.lab;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        Set<Long> t = new HashSet<>();
        t.add(1L);
        t.add(5L);
        t.add(2L);
        t.add(8L);

        List<Long> collect = t.stream().sorted().collect(Collectors.toList());

        for (Long l : collect) {
            System.out.println(l);
        }


    }
}
