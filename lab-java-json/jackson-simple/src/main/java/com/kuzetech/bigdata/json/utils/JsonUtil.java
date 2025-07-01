package com.kuzetech.bigdata.json.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;

public class JsonUtil {

    public static final ObjectMapper NUMBER_MAPPER = new ObjectMapper()
            // 避免将BigDecimal从Entity转换到JsonNode时以科学计数法表示.
            .setNodeFactory(JsonNodeFactory.withExactBigDecimals(true))
            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            .enable(USE_BIG_DECIMAL_FOR_FLOATS);

    public static final ObjectMapper NORMAL_MAPPER = new ObjectMapper();

    public static void deleteElementByPath(ObjectNode topNode, String removePath) {
        String removeElementIndex = removePath.substring(removePath.lastIndexOf('/') + 1);
        JsonPointer parentJsonPointer = JsonPointer.compile(removePath.substring(0, removePath.lastIndexOf('/')));
        JsonNode parentNode = topNode.at(parentJsonPointer);
        if (parentNode.isArray()) {
            ((ArrayNode) parentNode).remove(Integer.parseInt(removeElementIndex));
        } else if (parentNode.isObject()) {
            ((ObjectNode) parentNode).remove(removeElementIndex);
        }
    }
}
