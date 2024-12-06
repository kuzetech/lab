package com.kuzetech.bigdata.json.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    @JsonProperty(value = "user_name", required = true) // 指定映射名称为 "user_name"，并设置为必填字段
    private String name;

    @JsonProperty(value = "user_age", required = true) // 指定映射名称为 "user_age"，并设置为必填字段
    private Integer age;
}
