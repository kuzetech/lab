package com.funnydb.mutation.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funnydb.mutation.model.*;
import com.funnydb.mutation.service.InvalidMutationMessageException;

public class MutationEventParser {
    private final ObjectMapper objectMapper;

    public MutationEventParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ConsumedMutationMessage parse(String topic, int partition, long offset, String key, String payload) {
        try {
            MutationEvent event = objectMapper.readValue(payload, MutationEvent.class);
            validate(event);
            return new ConsumedMutationMessage(topic, partition, offset, key, event);
        } catch (InvalidMutationMessageException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new InvalidMutationMessageException("Invalid mutation payload", exception);
        }
    }

    private void validate(MutationEvent event) {
        if (event == null) {
            throw new InvalidMutationMessageException("Missing event");
        }
        if (isBlank(event.getApp())) {
            throw new InvalidMutationMessageException("Missing app");
        }
        if (isBlank(event.getType())) {
            throw new InvalidMutationMessageException("Missing type");
        }
        if (MutationTargetType.fromEventType(event.getType()) == null) {
            throw new InvalidMutationMessageException("Unsupported type " + event.getType());
        }
        if (event.getData() == null) {
            throw new InvalidMutationMessageException("Missing data");
        }
        MutationData data = event.getData();
        if (isBlank(data.getIdentify())) {
            throw new InvalidMutationMessageException("Missing #identify");
        }
        if (data.getTime() == null) {
            throw new InvalidMutationMessageException("Missing #time");
        }
        if (isBlank(data.getDataLifecycle())) {
            throw new InvalidMutationMessageException("Missing #data_lifecycle");
        }
        if (isBlank(data.getOperate())) {
            throw new InvalidMutationMessageException("Missing #operate");
        }
        if (MutationOperate.fromValue(data.getOperate()) == null) {
            throw new InvalidMutationMessageException("Unsupported #operate " + data.getOperate());
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
