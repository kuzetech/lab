package com.kuzetech.bigdata.study.sql;

import java.io.Serializable;

public class Employee implements Serializable {
    private String name;
    private long salary;

    // Constructors, getters, setters...


    public Employee() {
    }

    public Employee(String name, long salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }
}
