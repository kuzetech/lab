package com.kuzetech.bigdata.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuzetech.bigdata.json.domain.JacksonAnnotationObject;
import com.kuzetech.bigdata.json.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class AnnotationTest {

    @Test
    public void test() throws JsonProcessingException {
        JacksonAnnotationObject annotationObject;
        try (InputStream inputStream = AnnotationTest.class.getClassLoader().getResourceAsStream("data/annotation.json")) {
            annotationObject = JacksonUtil.BIG_DECIMAL_MAPPER.readValue(inputStream, JacksonAnnotationObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(JacksonUtil.BIG_DECIMAL_MAPPER.writeValueAsString(annotationObject));
    }
}
