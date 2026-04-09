package com.funnydb.mutation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funnydb.mutation.infra.RedisLuaResultParser;
import com.funnydb.mutation.service.MutationApplyResult;
import com.funnydb.mutation.service.MutationStatus;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class RedisLuaResultParserTests {

    @Test
    public void shouldParseLuaEvalResult() {
        RedisLuaResultParser parser = new RedisLuaResultParser(new ObjectMapper());
        MutationApplyResult result = parser.parse(Arrays.asList(
                "INVALID_FIELD_TYPE",
                "{\"data\":{\"score\":3},\"metadata\":{\"last_event_time\":1000,\"#created_time\":999,\"#data_lifecycle\":\"0\"}}",
                "[\"name\"]"
        ));

        assertEquals(MutationStatus.INVALID_FIELD_TYPE, result.getStatus());
        assertEquals(3, ((Number) result.getDocument().getData().get("score")).intValue());
        assertEquals(1000L, result.getDocument().getMetadata().getLastEventTime().longValue());
        assertEquals("name", result.getIgnoredFields().get(0));
    }
}
