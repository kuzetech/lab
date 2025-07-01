package com.kuzetech.bigdata.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzetech.bigdata.json.domain.User;

public class RequiredApp {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        // 正确的 JSON 示例
        String validJson = "{\"user_name\":\"Alice\", \"user_age\":25}";

        // 缺少字段的 JSON 示例
        String invalidJson = "{\"user_name\":\"Alice\"}";

        try {
            // 正确的 JSON
            User validUser = mapper.readValue(validJson, User.class);
            System.out.println("Valid user: " + validUser);
        } catch (Exception e) {
            System.out.println("Failed to parse valid JSON: " + e.getMessage());
        }

        try {
            // 缺少必填字段的 JSON
            User invalidUser = mapper.readValue(invalidJson, User.class);
            System.out.println("Invalid user: " + invalidUser);
        } catch (Exception e) {
            System.out.println("Failed to parse invalid JSON: " + e.getMessage());
        }
    }
}
