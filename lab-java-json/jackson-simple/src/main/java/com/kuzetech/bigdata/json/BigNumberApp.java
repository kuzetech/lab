package com.kuzetech.bigdata.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kuzetech.bigdata.json.utils.JsonUtil;

import java.io.IOException;
import java.io.InputStream;

public class BigNumberApp {
    public static void main(String[] args) {
        try (
                InputStream inputStream = BigNumberApp.class.getClassLoader().getResourceAsStream("data-big-number.json");
        ) {
            ObjectNode objectNode = JsonUtil.NUMBER_MAPPER.readValue(inputStream, ObjectNode.class);
            JsonNode jsonNode = objectNode.get("pay_sum_out");
            System.out.println(jsonNode.getNodeType());
            System.out.println(jsonNode.canConvertToLong());
            System.out.println(JsonUtil.NUMBER_MAPPER.writeValueAsString(objectNode));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
