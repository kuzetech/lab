package com.funnydb.mutation.service;

import com.funnydb.mutation.model.StoredDocument;

import java.util.Collections;
import java.util.List;

public class MutationApplyResult {
    private final MutationStatus status;
    private final StoredDocument document;
    private final List<String> ignoredFields;

    public MutationApplyResult(MutationStatus status, StoredDocument document, List<String> ignoredFields) {
        this.status = status;
        this.document = document;
        this.ignoredFields = ignoredFields == null ? Collections.emptyList() : ignoredFields;
    }

    public MutationStatus getStatus() {
        return status;
    }

    public StoredDocument getDocument() {
        return document;
    }

    public List<String> getIgnoredFields() {
        return ignoredFields;
    }
}
