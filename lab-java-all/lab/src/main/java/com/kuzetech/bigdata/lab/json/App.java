package com.kuzetech.bigdata.lab.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = "{}";
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(content);
            System.out.println(1);
            Integer instanceId = jsonNode.get("id").asInt();
            System.out.println(instanceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
