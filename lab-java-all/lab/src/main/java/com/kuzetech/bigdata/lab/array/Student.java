package com.kuzetech.bigdata.lab.array;

public class Student implements Comparable<Student> {
    public Long age;

    public Student(Long age) {
        this.age = age;
    }

    @Override
    public int compareTo(Student o) {
        return -this.age.compareTo(o.age);
    }
}
