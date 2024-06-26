package com.kuzetech.bigdata.lab.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeTypeJudge {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Student a = new Student("a", 1, 2L, 1.1F, 1.2);
        String s = mapper.writeValueAsString(a);
        System.out.println(s);
        JsonNode jsonNode = mapper.readValue(s, JsonNode.class);

        JsonNode age1Node = jsonNode.get("age1");
        JsonNode age2Node = jsonNode.get("age2");
        JsonNode money1Node = jsonNode.get("money1");
        JsonNode money2Node = jsonNode.get("money2");

        System.out.println(age1Node.isInt());
        System.out.println(age1Node.isLong());
        System.out.println(age1Node.isFloat());
        System.out.println(age1Node.isDouble());

        System.out.println(age2Node.isInt());
        System.out.println(age2Node.isLong());
        System.out.println(age2Node.isFloat());
        System.out.println(age2Node.isDouble());

        System.out.println(money1Node.isInt());
        System.out.println(money1Node.isLong());
        System.out.println(money1Node.isFloat());
        System.out.println(money1Node.isDouble());

        System.out.println(money2Node.isInt());
        System.out.println(money2Node.isLong());
        System.out.println(money2Node.isFloat());
        System.out.println(money2Node.isDouble());


        String content = "{\"event\":\"login\",\"date\":-7.51999998092651}";

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
