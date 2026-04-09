package com.funnydb.mutation.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funnydb.mutation.model.StoredDocument;
import com.funnydb.mutation.service.MutationApplyResult;
import com.funnydb.mutation.service.MutationStatus;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RedisLuaResultParser {
    private final ObjectMapper objectMapper;

    public RedisLuaResultParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public MutationApplyResult parse(Object rawResult) {
        if (!(rawResult instanceof List)) {
            throw new IllegalStateException("Unexpected redis lua result: " + rawResult);
        }
        List<?> values = (List<?>) rawResult;
        if (values.size() < 3) {
            throw new IllegalStateException("Unexpected redis lua result size: " + values.size());
        }
        String status = String.valueOf(values.get(0));
        String documentJson = String.valueOf(values.get(1));
        String ignoredFieldsJson = String.valueOf(values.get(2));
        return new MutationApplyResult(
                MutationStatus.fromValue(status),
                readDocument(documentJson),
                readIgnoredFields(ignoredFieldsJson)
        );
    }

    private StoredDocument readDocument(String documentJson) {
        try {
            return objectMapper.readValue(documentJson, StoredDocument.class);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to deserialize redis lua document", exception);
        }
    }

    private List<String> readIgnoredFields(String ignoredFieldsJson) {
        try {
            return objectMapper.readValue(ignoredFieldsJson, new TypeReference<List<String>>() {
            });
        } catch (IOException exception) {
            return Collections.emptyList();
        }
    }
}
