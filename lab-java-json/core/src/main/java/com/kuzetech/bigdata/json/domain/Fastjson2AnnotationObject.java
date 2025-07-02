package com.kuzetech.bigdata.json.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fastjson2AnnotationObject {

    private Integer id;
    @JSONField(name = "name")
    private String name;
    private String email;

    // 用于存储未知字段
    public Map<String, Object> extraFields = new HashMap<>();

}
