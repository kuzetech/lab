package com.funnydb.mutation.service;

public class InvalidMutationMessageException extends RuntimeException {
    public InvalidMutationMessageException(String message) {
        super(message);
    }

    public InvalidMutationMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
