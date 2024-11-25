package com.kuzetech.bigdata.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kuzetech.bigdata.json.utils.JsonUtil;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class ValidatorRemoveApp {
    public static void main(String[] args) throws JsonProcessingException {

        JSONObject schemaNode;
        try (InputStream inputStream = ValidatorRemoveApp.class.getClassLoader().getResourceAsStream("schema-book.json")) {
            assert inputStream != null;
            schemaNode = new JSONObject(new JSONTokener(inputStream));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject dataNode;
        ObjectNode dataObjectNode;
        try (InputStream inputStream = ValidatorRemoveApp.class.getClassLoader().getResourceAsStream("data-book-err.json")) {
            assert inputStream != null;
            String content = new String(inputStream.readAllBytes());
            dataNode = new JSONObject(new JSONTokener(content));
            dataObjectNode = JsonUtil.NORMAL_MAPPER.readValue(content, ObjectNode.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 创建 Schema 对象
        Schema schema = SchemaLoader.load(schemaNode);

        // 执行验证
        try {
            schema.validate(dataNode);
        } catch (ValidationException e) {
            for (ValidationException c : e.getCausingExceptions()) {
                if (!"required".equalsIgnoreCase(c.getKeyword())) {
                    String path = c.getPointerToViolation().substring(1);
                    JsonUtil.deleteElementByPath(dataObjectNode, path);
                }
            }
        }

        System.out.println(JsonUtil.NORMAL_MAPPER.writeValueAsString(dataObjectNode));


    }
}
