package com.kuzetech.bigdata.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.kuzetech.bigdata.json.utils.JsonUtil;

import java.io.InputStream;

public class ValidatorApp {
    public static void main(String[] args) {

        JsonNode schemaNode;
        try (InputStream inputStream = ValidatorApp.class.getClassLoader().getResourceAsStream("schema-book.json")) {
            schemaNode = JsonUtil.NORMAL_MAPPER.readTree(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JsonNode dataNode;
        try (InputStream inputStream = ValidatorApp.class.getClassLoader().getResourceAsStream("data-book-err.json")) {
            dataNode = JsonUtil.NORMAL_MAPPER.readTree(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        ProcessingReport result = JsonSchemaFactory.byDefault().getValidator()
                .validateUnchecked(schemaNode, dataNode);

        if (result.isSuccess()) {
            System.out.println("valid success...");
        } else {
            // 遇到第一个错误就会停下来
            for (ProcessingMessage message : result) {
                System.out.println(message.asJson());
                // JsonNode errorNode = message.asJson();
            }
        }
    }
}
