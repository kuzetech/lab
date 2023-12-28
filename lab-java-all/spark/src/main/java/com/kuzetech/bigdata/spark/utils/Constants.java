package com.kuzetech.bigdata.spark.utils;

import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final List<String> wordList = new ArrayList<>();

    static {
        wordList.add("a");
        wordList.add("b");
        wordList.add("c");
        wordList.add("d");
        wordList.add("e");
        wordList.add("a");
    }


    public static final List<Person> PersonList = new ArrayList<>();

    static {
        PersonList.add(new Person("amy", 11));
        PersonList.add(new Person("bob", 12));
    }


    public static final List<Tuple2<String, Integer>> tupleList = new ArrayList<>();

    static {
        tupleList.add(new Tuple2<>("amy", 11));
        tupleList.add(new Tuple2<>("bob", 12));
    }

}
