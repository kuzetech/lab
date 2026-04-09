package com.funnydb.mutation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MutationPollBatch {
    private final List<ConsumedMutationMessage> messages;
    private final int invalidCount;

    public MutationPollBatch(List<ConsumedMutationMessage> messages, int invalidCount) {
        this.messages = new ArrayList<>(messages);
        this.invalidCount = invalidCount;
    }

    public List<ConsumedMutationMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public int getInvalidCount() {
        return invalidCount;
    }

    public boolean isEmpty() {
        return messages.isEmpty() && invalidCount == 0;
    }
}
