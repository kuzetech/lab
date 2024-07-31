package com.kuzetech.bigdata.lab.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;

public class App {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper()
                // 避免将BigDecimal从Entity转换到JsonNode时以科学计数法表示.
                .setNodeFactory(JsonNodeFactory.withExactBigDecimals(true))
                .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                .enable(USE_BIG_DECIMAL_FOR_FLOATS);


        String content = "{\"pay_sum\":49.9001,\"pay_sum_out\":49.90000000000001}";
        ObjectNode objectNode = objectMapper.readValue(content, ObjectNode.class);
        JsonNode jsonNode = objectNode.get("big_sum");
        System.out.println(jsonNode.canConvertToLong());

        System.out.println(objectMapper.writeValueAsString(objectNode));
    }

}
