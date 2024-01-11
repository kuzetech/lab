package com.kuzetech.bigdata.lab.json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {
    private String name;
    private Integer age1;
    private Long age2;
    private Float money1;
    private Double money2;

    public Student(String name, Integer age1, Long age2, Float money1, Double money2) {
        this.name = name;
        this.age1 = age1;
        this.age2 = age2;
        this.money1 = money1;
        this.money2 = money2;
    }
}
