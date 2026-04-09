package com.funnydb.mutation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funnydb.mutation.infra.KafkaMutationConsumer;
import com.funnydb.mutation.infra.MutationEventParser;
import com.funnydb.mutation.model.ConsumedMutationMessage;
import com.funnydb.mutation.model.MutationPollBatch;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class KafkaMutationConsumerTests {

    @Test
    public void shouldPollAndParseKafkaRecords() {
        MockConsumer<String, String> consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        KafkaMutationConsumer mutationConsumer = new KafkaMutationConsumer(
                consumer,
                new MutationEventParser(new ObjectMapper()),
                "funnydb-ingest-mutation-events",
                Duration.ofMillis(1)
        );
        TopicPartition partition = new TopicPartition("funnydb-ingest-mutation-events", 0);
        consumer.rebalance(List.of(partition));
        consumer.updateBeginningOffsets(java.util.Collections.singletonMap(partition, 0L));
        consumer.addRecord(new ConsumerRecord<>(
                "funnydb-ingest-mutation-events",
                0,
                0L,
                "u-1",
                "{\"app\":\"demo\",\"type\":\"UserMutation\",\"data\":{\"#operate\":\"set\",\"#time\":1000,\"#identify\":\"u-1\",\"#data_lifecycle\":\"0\",\"properties\":{\"score\":1}}}"
        ));

        MutationPollBatch pollBatch = mutationConsumer.poll();
        List<ConsumedMutationMessage> messages = pollBatch.getMessages();

        assertEquals(1, messages.size());
        assertEquals(0, pollBatch.getInvalidCount());
        assertEquals("u-1", messages.get(0).getEvent().getData().getIdentify());
        mutationConsumer.close();
    }

    @Test
    public void shouldSkipInvalidKafkaRecords() {
        MockConsumer<String, String> consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        KafkaMutationConsumer mutationConsumer = new KafkaMutationConsumer(
                consumer,
                new MutationEventParser(new ObjectMapper()),
                "funnydb-ingest-mutation-events",
                Duration.ofMillis(1)
        );
        TopicPartition partition = new TopicPartition("funnydb-ingest-mutation-events", 0);
        consumer.rebalance(List.of(partition));
        consumer.updateBeginningOffsets(java.util.Collections.singletonMap(partition, 0L));
        consumer.addRecord(new ConsumerRecord<>(
                "funnydb-ingest-mutation-events",
                0,
                0L,
                "u-1",
                "{\"type\":\"UserMutation\"}"
        ));

        MutationPollBatch pollBatch = mutationConsumer.poll();

        assertEquals(0, pollBatch.getMessages().size());
        assertEquals(1, pollBatch.getInvalidCount());
        mutationConsumer.close();
    }
}
