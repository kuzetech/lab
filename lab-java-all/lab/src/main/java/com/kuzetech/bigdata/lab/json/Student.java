package com.kuzetech.bigdata.lab.json;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge1() {
        return age1;
    }

    public void setAge1(Integer age1) {
        this.age1 = age1;
    }

    public Long getAge2() {
        return age2;
    }

    public void setAge2(Long age2) {
        this.age2 = age2;
    }

    public Float getMoney1() {
        return money1;
    }

    public void setMoney1(Float money1) {
        this.money1 = money1;
    }

    public Double getMoney2() {
        return money2;
    }

    public void setMoney2(Double money2) {
        this.money2 = money2;
    }
}
