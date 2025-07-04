package com.kuzetech.bigdata.json.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FastjsonAnnotationObject {

    // @JSONField(serialize = false, deserialize = false)
    private Integer id;
    @JSONField(name = "name")
    private String name;
    // 序列化时默认不输出 null 值字段，如果要输出需要设置
    // @JSONField(serialzeFeatures = SerializerFeature.WriteMapNullValue)
    private String email;

    // 用于存储未知字段, 不支持平铺输出
    @JSONField(unwrapped = true)
    public Map<String, Object> extraFields = new HashMap<>();
}
