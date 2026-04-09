package com.funnydb.mutation.service;

public enum MutationStatus {
    APPLIED,
    IGNORED_OLD_EVENT,
    INVALID_FIELD_TYPE;

    public static MutationStatus fromValue(String value) {
        for (MutationStatus status : values()) {
            if (status.name().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unsupported mutation status: " + value);
    }
}
