package com.kuzetech.bigdata.kafka.commitManual;

public class AsyncCommitOffsetsConsecutiveErrorException extends RuntimeException {

    public AsyncCommitOffsetsConsecutiveErrorException(String message) {
        super(message);
    }
}
