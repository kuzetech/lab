package com.kuzetech.bigdata.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kuzetech.bigdata.json.util.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

class BigNumberTest {

    @Test
    public void test() {
        try (
                InputStream inputStream = BigNumberTest.class.getClassLoader().getResourceAsStream("data/big-number.json");
        ) {
            ObjectNode objectNode = JacksonUtil.BIG_DECIMAL_MAPPER.readValue(inputStream, ObjectNode.class);
            JsonNode jsonNode = objectNode.get("pay_sum_out");
            System.out.println(jsonNode.getNodeType());
            System.out.println(jsonNode.canConvertToLong());
            System.out.println(JacksonUtil.BIG_DECIMAL_MAPPER.writeValueAsString(objectNode));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}