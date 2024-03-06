package com.kuzetech.bigdata.kafka.commitManual;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OffsetsCommitAsyncCallback implements OffsetCommitCallback {

    public static final Logger logger = LoggerFactory.getLogger(OffsetsCommitAsyncCallback.class);

    private Integer errorCount = 0;

    @Override
    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
        if (exception != null) {
            errorCount++;
            logger.error("Async Commit Offsets Error", exception);
            if (errorCount >= 3) {
                throw new AsyncCommitOffsetsConsecutiveErrorException("Async commit offsets more than three consecutive errors");
            }
        } else {
            errorCount = 0;
        }
    }
}
