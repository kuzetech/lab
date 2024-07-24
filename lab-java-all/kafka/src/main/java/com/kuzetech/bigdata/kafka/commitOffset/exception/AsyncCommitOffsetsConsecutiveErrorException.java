package com.kuzetech.bigdata.kafka.commitOffset.exception;

public class AsyncCommitOffsetsConsecutiveErrorException extends RuntimeException {

    public AsyncCommitOffsetsConsecutiveErrorException(String message) {
        super(message);
    }
}
