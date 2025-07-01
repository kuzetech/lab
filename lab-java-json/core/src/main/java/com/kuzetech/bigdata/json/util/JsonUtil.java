package com.kuzetech.bigdata.json.util;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtil {

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
