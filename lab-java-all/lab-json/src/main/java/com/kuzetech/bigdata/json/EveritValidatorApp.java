package com.kuzetech.bigdata.json;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class EveritValidatorApp {
    public static void main(String[] args) {

        JSONObject schemaNode;
        try (InputStream inputStream = EveritValidatorApp.class.getClassLoader().getResourceAsStream("schema-book.json")) {
            assert inputStream != null;
            schemaNode = new JSONObject(new JSONTokener(inputStream));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject dataNode;
        try (InputStream inputStream = EveritValidatorApp.class.getClassLoader().getResourceAsStream("data-book-extra.json")) {
            assert inputStream != null;
            dataNode = new JSONObject(new JSONTokener(inputStream));
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
                System.out.println(c.toJSON());
            }
        }
    }
}
