package com.funnydb.mutation.infra;

import com.funnydb.mutation.model.ConsumedMutationMessage;
import com.funnydb.mutation.model.MutationPollBatch;
import com.funnydb.mutation.service.InvalidMutationMessageException;
import com.funnydb.mutation.service.MutationMetrics;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class KafkaMutationConsumer implements MutationConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaMutationConsumer.class);

    private final Consumer<String, String> consumer;
    private final MutationEventParser parser;
    private final Duration pollTimeout;
    private final Runnable partitionsRevokedCallback;
    private final MutationMetrics metrics;

    public KafkaMutationConsumer(Consumer<String, String> consumer,
                                 MutationEventParser parser,
                                 String inputTopic,
                                 Duration pollTimeout) {
        this(consumer, parser, inputTopic, pollTimeout, () -> {
        }, new MutationMetrics());
    }

    public KafkaMutationConsumer(Consumer<String, String> consumer,
                                 MutationEventParser parser,
                                 String inputTopic,
                                 Duration pollTimeout,
                                 Runnable partitionsRevokedCallback,
                                 MutationMetrics metrics) {
        this.consumer = consumer;
        this.parser = parser;
        this.pollTimeout = pollTimeout;
        this.partitionsRevokedCallback = partitionsRevokedCallback;
        this.metrics = metrics;
        this.consumer.subscribe(Collections.singletonList(inputTopic), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                if (!partitions.isEmpty()) {
                    LOG.info("Partitions revoked: {}", partitions);
                    KafkaMutationConsumer.this.metrics.recordRebalance();
                    KafkaMutationConsumer.this.partitionsRevokedCallback.run();
                }
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                if (!partitions.isEmpty()) {
                    LOG.info("Partitions assigned: {}", partitions);
                }
            }
        });
    }

    @Override
    public MutationPollBatch poll() {
        long startNanos = System.nanoTime();
        ConsumerRecords<String, String> records = consumer.poll(pollTimeout);
        metrics.recordPoll(records.count(), System.nanoTime() - startNanos);
        List<ConsumedMutationMessage> messages = new ArrayList<>();
        int invalidCount = 0;
        for (ConsumerRecord<String, String> record : records) {
            try {
                messages.add(parser.parse(
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.key(),
                        record.value()
                ));
            } catch (InvalidMutationMessageException exception) {
                invalidCount++;
                LOG.warn("数据异常 {}，消息内容：{}", exception, record.value());
            }
        }
        if (invalidCount > 0) {
            metrics.recordInvalidMessages(invalidCount);
        }
        return new MutationPollBatch(messages, invalidCount);
    }

    @Override
    public void close() {
        consumer.close();
    }
}
