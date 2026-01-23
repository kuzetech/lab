package com.kuzetech.bigdata.json.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;

public class JacksonUtil {

    public static final ObjectMapper NORMAL_MAPPER = new ObjectMapper();

    public static final ObjectMapper BIG_DECIMAL_MAPPER = new ObjectMapper()
            // 避免将BigDecimal从Entity转换到JsonNode时以科学计数法表示.
            .setNodeFactory(JsonNodeFactory.withExactBigDecimals(true))
            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            .enable(USE_BIG_DECIMAL_FOR_FLOATS);

}
