package com.kuzetech.bigdata.lab.json.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class App {
    private static final ObjectMapper mapper = new ObjectMapper();

    static final String json1 = "{\"a\":1}";
    static final String json1_schema = "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"type\":\"object\",\"properties\":{\"a\":{\"type\":\"number\"}}}";

    public static void main(String[] args) throws JsonProcessingException {
        ProcessingReport report = JsonSchemaFactory.byDefault().getValidator()
                .validateUnchecked(mapper.readTree(json1_schema), mapper.readTree(json1));
        if (report.isSuccess()) {
            System.out.println("valid success...");
        } else {
            List<JsonNode> errorsJsonArray = new ArrayList<>();
            Iterator<ProcessingMessage> iterator = report.iterator();
            while (iterator.hasNext()) {
                errorsJsonArray.add(iterator.next().asJson());
            }
            System.out.println(errorsJsonArray.toString());
        }
    }
}
