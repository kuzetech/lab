package com.kuzetech.bigdata.json;

import com.alibaba.fastjson.JSON;
import com.kuzetech.bigdata.json.domain.ObjectNodeContent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class JacksonTest {

    @Test
    void StringToObject() {
        String jsonStr = "{'password':'123456','username':'demo','data':{'test':'any'}}";
        ObjectNodeContent content = JSON.parseObject(jsonStr, ObjectNodeContent.class);
        // 无法完成 ObjectNode 对象反序列化
        System.out.println(content);
    }
}