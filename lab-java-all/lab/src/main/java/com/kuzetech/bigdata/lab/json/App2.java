package com.kuzetech.bigdata.lab.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class App2 {
    public static void main(String[] args) throws JsonProcessingException {
        String content = "{\"event\":\"login\",\"date\":-7.51999998092651}";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.readValue(content, ObjectNode.class);
        JsonNode node = objectNode.get("date");
        System.out.println(node.getNodeType());


        System.out.println(node.isShort());
        System.out.println(node.isInt());
        System.out.println(node.isLong());
        System.out.println(node.isFloat());
        System.out.println(node.isDouble());

    }
}
