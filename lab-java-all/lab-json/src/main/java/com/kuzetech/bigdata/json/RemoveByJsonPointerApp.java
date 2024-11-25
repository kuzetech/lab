package com.kuzetech.bigdata.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kuzetech.bigdata.json.utils.JsonUtil;

import java.io.InputStream;

public class RemoveByJsonPointerApp {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectNode data;
        try (
                InputStream inputStream = RemoveByJsonPointerApp.class.getClassLoader().getResourceAsStream("data-complex.json");
        ) {
            data = JsonUtil.NORMAL_MAPPER.readValue(inputStream, ObjectNode.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println(JsonUtil.NORMAL_MAPPER.writeValueAsString(data));

        JsonUtil.deleteElementByPath(data, "/glossary/GlossDiv/GlossList/GlossEntry/GlossDef/GlossSeeAlso/1");

        System.out.println(JsonUtil.NORMAL_MAPPER.writeValueAsString(data));

    }
}


