package com.kuzetech.bigdata.json;

import com.alibaba.fastjson.JSON;
import com.kuzetech.bigdata.json.domain.JsonObjectContent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class JsonObjectTest {

    @Test
    void StringToObject() {
        String jsonStr = "{'password':'123456','username':'demo','data':{'test':'any'}}";
        JsonObjectContent content = JSON.parseObject(jsonStr, JsonObjectContent.class);
        Assertions.assertNotNull(content.getData());
    }
}