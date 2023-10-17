package com.kuzetech.bigdata.study.array;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        Student[] ss = {new Student(1L),new Student(3L),new Student(2L)};

        Arrays.sort(ss);

        for (Student s : ss) {
            System.out.println(s.age);
        }
    }
}
