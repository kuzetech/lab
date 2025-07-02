package com.kuzetech.bigdata.json;

import com.alibaba.fastjson.JSON;
import com.kuzetech.bigdata.json.domain.Account;
import com.kuzetech.bigdata.json.domain.FastjsonAnnotationObject;
import com.kuzetech.bigdata.json.processor.FastjsonAnnotationObjectExtraProcessor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AdditionalFieldTest {

    @Test
    public void test() {
        String jsonStr = "{'pay': 123333333, 'username': 'test'}";
        Account account = JSON.parseObject(jsonStr, Account.class);
        // 即使有多余字段不会像 jackson 报错，缺失字段也会默认为 null
        System.out.println(account);
    }

    @Test
    public void testGetExtraField() {
        FastjsonAnnotationObject annotationObject;
        try (InputStream inputStream = AnnotationTest.class.getClassLoader().getResourceAsStream("data/annotation.json")) {
            String jsonStr = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            annotationObject = JSON.parseObject(jsonStr, FastjsonAnnotationObject.class, new FastjsonAnnotationObjectExtraProcessor());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(annotationObject);
        System.out.println(JSON.toJSONString(annotationObject));
    }
}
