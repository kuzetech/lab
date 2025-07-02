package com.kuzetech.bigdata.json;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuzetech.bigdata.json.domain.FastjsonAnnotationObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class AnnotationTest {

    @Test
    public void test() throws JsonProcessingException {
        FastjsonAnnotationObject annotationObject;
        try (InputStream inputStream = AnnotationTest.class.getClassLoader().getResourceAsStream("data/annotation.json")) {
            annotationObject = JSON.parseObject(inputStream, FastjsonAnnotationObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(annotationObject);
        System.out.println(JSON.toJSONString(annotationObject));
    }
}
