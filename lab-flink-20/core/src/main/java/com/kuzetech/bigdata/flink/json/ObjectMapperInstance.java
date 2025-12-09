package com.kuzetech.bigdata.flink.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.io.Serializable;

public class ObjectMapperInstance implements Serializable {
    private final ObjectMapper instance = new ObjectMapper()
            // 避免将BigDecimal从Entity转换到JsonNode时以科学计数法表示.
            .setNodeFactory(JsonNodeFactory.withExactBigDecimals(true))
            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

    /**
     * 获取通用的ObjectMapper
     *
     * @return 通用的ObjectMapper
     */
    public static ObjectMapper getInstance() {
        return Instance.INSTANCE.instance;
    }

    private static class Instance {
        public static final ObjectMapperInstance INSTANCE = new ObjectMapperInstance();
    }
}
