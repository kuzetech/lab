package com.funnydb.mutation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funnydb.mutation.infra.MutationEventParser;
import com.funnydb.mutation.model.ConsumedMutationMessage;
import com.funnydb.mutation.service.InvalidMutationMessageException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class MutationEventParserTests {

    @Test
    public void shouldParseValidPayload() {
        MutationEventParser parser = new MutationEventParser(new ObjectMapper());
        ConsumedMutationMessage message = parser.parse(
                "funnydb-ingest-mutation-events",
                1,
                10L,
                "u-1",
                "{\"app\":\"demo\",\"type\":\"UserMutation\",\"data\":{\"#operate\":\"set\",\"#time\":1000,\"#identify\":\"u-1\",\"#data_lifecycle\":\"0\",\"properties\":{\"name\":\"alex\"}}}"
        );

        assertEquals("demo", message.getEvent().getApp());
        assertEquals("UserMutation", message.getEvent().getType());
        assertEquals("u-1", message.getEvent().getData().getIdentify());
        assertEquals(1000L, message.getEvent().getData().getTime().longValue());
    }

    @Test
    public void shouldRejectMissingRequiredFields() {
        MutationEventParser parser = new MutationEventParser(new ObjectMapper());

        assertThrows(InvalidMutationMessageException.class, () -> parser.parse(
                "funnydb-ingest-mutation-events",
                1,
                10L,
                "u-1",
                "{\"type\":\"UserMutation\",\"data\":{\"#operate\":\"set\",\"#time\":1000,\"#identify\":\"u-1\",\"#data_lifecycle\":\"0\"}}"
        ));
    }
}
