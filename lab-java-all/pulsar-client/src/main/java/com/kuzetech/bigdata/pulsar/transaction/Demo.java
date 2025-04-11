package com.kuzetech.bigdata.pulsar.transaction;

import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.api.transaction.Transaction;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Demo {
    public static void main(String[] args) throws PulsarClientException {
        PulsarClient client = PulsarClient.builder()
                // Step 3: create a Pulsar client and enable transactions.
                .enableTransaction(true)
                .serviceUrl("pulsar://localhost:6650")
                .build();

        Producer<String> sourceProducer = client.newProducer(Schema.STRING)
                .producerName("my-producer-name")
                .topic("public/default/source")
                .sendTimeout(0, TimeUnit.SECONDS)
                .create();

        int count = 5;
        // Step 5: produce messages to input topics.
        for (int i = 0; i < count; i++) {
            sourceProducer.send("Hello Pulsar! count : " + i);
        }

        // Step 4: create consumers to consume messages from input and output topics.
        Consumer<String> inputConsumer = client.newConsumer(Schema.STRING)
                .subscriptionName("my-subscription-name")
                .topic("public/default/source")
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscribe();

        // Step 4: create producers to produce messages to input and output topics.
        Producer<String> outputProducer = client.newProducer(Schema.STRING)
                .producerName("my-producer-name")
                .topic("public/default/output")
                .sendTimeout(0, TimeUnit.SECONDS)
                .create();

        // Step 5: consume messages and produce them to output topics with transactions.
        for (int i = 0; i < count; i++) {
            // Step 5: the consumer successfully receives messages.
            Message<String> message = inputConsumer.receive();

            // Step 6: create transactions.
            // The transaction timeout is specified as 10 seconds.
            // If the transaction is not committed within 10 seconds, the transaction is automatically aborted.
            Transaction txn = null;
            try {
                txn = client.newTransaction()
                        .withTransactionTimeout(10, TimeUnit.SECONDS)
                        .build()
                        .get();

                // Step 7: the producers produce messages to output topics with transactions
                outputProducer.newMessage(txn).value("Hello Pulsar! outputTopicOne count : " + i).send();

                // Step 7: the consumers acknowledge the input message with the transactions *individually*.
                inputConsumer.acknowledgeAsync(message.getMessageId(), txn).get();

                // Step 8: commit transactions.
                txn.commit().get();
            } catch (ExecutionException e) {
                if (!(e.getCause() instanceof PulsarClientException.TransactionConflictException)) {
                    // If TransactionConflictException is not thrown,
                    // you need to redeliver or negativeAcknowledge this message,
                    // or else this message will not be received again.
                    inputConsumer.negativeAcknowledge(message);
                }

                // If a new transaction is created,
                // then the old transaction should be aborted.
                if (txn != null) {
                    txn.abort();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Consumer<String> outputConsumer = client.newConsumer(Schema.STRING)
                .subscriptionName("your-subscription-name")
                .topic("public/default/output")
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscribe();

        for (int i = 0; i < count; i++) {
            Message<String> message = outputConsumer.receive();
            System.out.println("Receive transaction message: " + message.getValue());
        }

    }
}
