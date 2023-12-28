package com.kuzetech.bigdata.lab.array;

import org.jetbrains.annotations.NotNull;

public class Student implements Comparable<Student>{
    public Long age;

    public Student(Long age) {
        this.age = age;
    }

    @Override
    public int compareTo(@NotNull Student o) {
        return -this.age.compareTo(o.age);
    }
}
