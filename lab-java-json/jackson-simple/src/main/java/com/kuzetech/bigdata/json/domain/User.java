package com.kuzetech.bigdata.json.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {

    private String name;
    private Integer age;
    private Student student;

    @JsonCreator
    public User(
            @JsonProperty(value = "user_name", required = true) String name,
            @JsonProperty(value = "user_age", required = true) Integer age,
            @JsonProperty(value = "student") Student student
    ) {
        this.name = name;
        this.age = age;
        this.student = student;
    }
}
