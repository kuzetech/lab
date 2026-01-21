package com.kuzetech.bigdata.pulsar.eos.manager;

import com.kuzetech.bigdata.pulsar.eos.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PulsarProducerManager implements AutoCloseable {
    private final PulsarClient client;
    private final Producer<byte[]> producer;
    private final boolean transactionEnabled;
    private final long transactionTimeoutMs;

    public PulsarProducerManager(AppConfig.PulsarConfig config) throws PulsarClientException {
        log.info("Initializing Pulsar client: {}", config.getServiceUrl());

        ClientBuilder clientBuilder = PulsarClient.builder()
                .serviceUrl(config.getServiceUrl())
                .operationTimeout(30, TimeUnit.SECONDS)
                .connectionTimeout(30, TimeUnit.SECONDS);

        this.transactionEnabled = config.getProducer().getEnableTransaction();
        this.transactionTimeoutMs = config.getProducer().getTransactionTimeoutMs();

        // 尝试启用事务支持（使用反射处理不同版本的 Pulsar API）
        if (transactionEnabled) {
            try {
                Method enableTransactionMethod = clientBuilder.getClass().getMethod("enableTransaction", boolean.class);
                enableTransactionMethod.invoke(clientBuilder, true);
                log.info("Transaction support enabled");
            } catch (Exception e) {
                log.warn("Failed to enable transaction support, will use non-transactional mode: {}", e.getMessage());
            }
        }

        this.client = clientBuilder.build();

        log.info("Creating producer for topic: {}", config.getTopic());
        ProducerBuilder<byte[]> producerBuilder = client.newProducer()
                .topic(config.getTopic())
                .producerName(config.getProducer().getProducerName())
                .sendTimeout(config.getProducer().getSendTimeoutMs(), TimeUnit.MILLISECONDS)
                .blockIfQueueFull(config.getProducer().getBlockIfQueueFull())
                .enableBatching(config.getProducer().getEnableBatching());

        this.producer = producerBuilder.create();
        log.info("Pulsar producer created successfully");
    }

    /**
     * 创建新事务（使用反射处理 Transaction API）
     */
    public Object newTransaction() throws Exception {
        if (!transactionEnabled) {
            return null;
        }

        try {
            // 使用反射调用 client.newTransaction()
            Method newTransactionMethod = client.getClass().getMethod("newTransaction");
            Object transactionBuilder = newTransactionMethod.invoke(client);

            // 调用 withTransactionTimeout
            Method withTimeoutMethod = transactionBuilder.getClass()
                    .getMethod("withTransactionTimeout", long.class, TimeUnit.class);
            transactionBuilder = withTimeoutMethod.invoke(transactionBuilder, transactionTimeoutMs, TimeUnit.MILLISECONDS);

            // 调用 build()
            Method buildMethod = transactionBuilder.getClass().getMethod("build");
            Object futureTransaction = buildMethod.invoke(transactionBuilder);

            // 调用 get()
            Method getMethod = futureTransaction.getClass().getMethod("get");
            Object transaction = getMethod.invoke(futureTransaction);

            log.debug("Transaction created: {}", transaction);
            return transaction;
        } catch (Exception e) {
            log.error("Failed to create transaction", e);
            throw e;
        }
    }

    /**
     * 获取事务 ID（使用反射）
     */
    public String getTransactionId(Object transaction) {
        if (transaction == null) {
            return null;
        }

        try {
            Method getTxnIdMethod = transaction.getClass().getMethod("getTxnID");
            Object txnId = getTxnIdMethod.invoke(transaction);
            return txnId.toString();
        } catch (Exception e) {
            log.warn("Failed to get transaction ID", e);
            return "unknown";
        }
    }

    /**
     * 发送消息（支持事务）
     */
    public MessageId sendMessage(String message, Object txn) throws PulsarClientException {
        try {
            TypedMessageBuilder<byte[]> messageBuilder = producer.newMessage()
                    .value(message.getBytes());

            // 如果有事务，使用反射设置事务
            if (txn != null) {
                Method transactionMethod = messageBuilder.getClass().getMethod("transaction", txn.getClass().getInterfaces()[0]);
                messageBuilder = (TypedMessageBuilder<byte[]>) transactionMethod.invoke(messageBuilder, txn);
            }

            return messageBuilder.send();
        } catch (PulsarClientException e) {
            throw e;
        } catch (Exception e) {
            throw new PulsarClientException(e);
        }
    }

    /**
     * 提交事务（使用反射）
     */
    public void commitTransaction(Object transaction) throws Exception {
        if (transaction == null) {
            return;
        }

        try {
            Method commitMethod = transaction.getClass().getMethod("commit");
            Object future = commitMethod.invoke(transaction);
            Method getMethod = future.getClass().getMethod("get");
            getMethod.invoke(future);
            log.debug("Transaction committed: {}", transaction);
        } catch (Exception e) {
            log.error("Failed to commit transaction", e);
            throw e;
        }
    }

    /**
     * 中止事务（使用反射）
     */
    public void abortTransaction(Object transaction) throws Exception {
        if (transaction == null) {
            return;
        }

        try {
            Method abortMethod = transaction.getClass().getMethod("abort");
            Object future = abortMethod.invoke(transaction);
            Method getMethod = future.getClass().getMethod("get");
            getMethod.invoke(future);
            log.debug("Transaction aborted: {}", transaction);
        } catch (Exception e) {
            log.error("Failed to abort transaction", e);
            throw e;
        }
    }

    /**
     * 查询事务状态（使用反射）
     * 注意：这个功能需要 Pulsar Admin API 支持，可能在某些版本中不可用
     *
     * @return OPEN, COMMITTED, ABORTED, TIMEOUT, UNKNOWN
     */
    public String queryTransactionState(String transactionId) {
        try {
            // 尝试通过 Admin API 查询事务状态
            // 注意：这需要 PulsarAdmin 客户端支持
            log.warn("Transaction state query not fully implemented. TransactionId: {}", transactionId);
            log.warn("Returning UNKNOWN status. Consider implementing admin API query.");
            return "UNKNOWN";

            // TODO: 实现完整的事务状态查询
            // PulsarAdmin admin = PulsarAdmin.builder()
            //     .serviceHttpUrl(adminUrl)
            //     .build();
            // TransactionCoordinatorStats stats = admin.transactions()
            //     .getTransactionCoordinatorStats();
            // ... 查询具体事务状态

        } catch (Exception e) {
            log.error("Failed to query transaction state for: {}", transactionId, e);
            return "UNKNOWN";
        }
    }

    public boolean isTransactionEnabled() {
        return transactionEnabled;
    }

    public Producer<byte[]> getProducer() {
        return producer;
    }

    @Override
    public void close() {
        try {
            if (producer != null) {
                producer.close();
                log.info("Pulsar producer closed");
            }
            if (client != null) {
                client.close();
                log.info("Pulsar client closed");
            }
        } catch (PulsarClientException e) {
            log.error("Error closing Pulsar resources", e);
        }
    }
}
