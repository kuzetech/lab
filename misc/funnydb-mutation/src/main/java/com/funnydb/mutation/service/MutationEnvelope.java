package com.funnydb.mutation.service;

import java.util.LinkedHashMap;
import java.util.Map;

public class MutationEnvelope {
    private final MutationRouting routing;
    private final MutationApplyResult applyResult;
    private final Map<String, Object> snapshot;

    public MutationEnvelope(MutationRouting routing, MutationApplyResult applyResult, Map<String, Object> snapshot) {
        this.routing = routing;
        this.applyResult = applyResult;
        this.snapshot = new LinkedHashMap<>(snapshot);
    }

    public MutationRouting getRouting() {
        return routing;
    }

    public MutationApplyResult getApplyResult() {
        return applyResult;
    }

    public Map<String, Object> getSnapshot() {
        return snapshot;
    }
}
