package com.kuzetech.bigdata.pulsar.eos.service;

import com.kuzetech.bigdata.pulsar.eos.config.AppConfig;
import com.kuzetech.bigdata.pulsar.eos.model.TransactionRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.api.transaction.Transaction;
import org.apache.pulsar.client.api.transaction.TxnID;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Pulsar 消息发送服务
 * 支持事务消息发送，实现 Exactly Once Semantics
 */
@Slf4j
public class PulsarProducerService implements Closeable {

    private final PulsarClient pulsarClient;
    private final Producer<String> producer;
    private final AppConfig config;

    public PulsarProducerService(AppConfig config) throws PulsarClientException {
        this.config = config;

        // 创建 Pulsar 客户端，启用事务支持
        this.pulsarClient = PulsarClient.builder()
                .serviceUrl(config.getPulsarServiceUrl())
                .enableTransaction(true)
                .build();

        // 创建 Producer
        this.producer = pulsarClient.newProducer(Schema.STRING)
                .topic(config.getPulsarTopic())
                .producerName("eos-producer-" + System.currentTimeMillis())
                .sendTimeout(30, TimeUnit.SECONDS)
                .create();

        log.info("Pulsar Producer 创建成功，Topic: {}", config.getPulsarTopic());
    }

    /**
     * 创建新事务
     */
    public Transaction createTransaction() throws ExecutionException, InterruptedException {
        Transaction transaction = pulsarClient.newTransaction()
                .withTransactionTimeout(config.getTransactionTimeout(), TimeUnit.SECONDS)
                .build()
                .get();

        log.debug("创建新事务: {}", formatTxnId(transaction.getTxnID()));
        return transaction;
    }

    /**
     * 发送批次消息（在事务中）
     */
    public void sendBatchWithTransaction(List<String> messages, Transaction transaction)
            throws PulsarClientException {
        for (String message : messages) {
            producer.newMessage(transaction)
                    .value(message)
                    .send();
        }
        log.debug("在事务 {} 中发送了 {} 条消息", formatTxnId(transaction.getTxnID()), messages.size());
    }

    /**
     * 提交事务
     */
    public void commitTransaction(Transaction transaction) throws ExecutionException, InterruptedException {
        transaction.commit().get();
        log.info("事务已提交: {}", formatTxnId(transaction.getTxnID()));
    }

    /**
     * 回滚事务
     */
    public void abortTransaction(Transaction transaction) {
        try {
            transaction.abort().get();
            log.info("事务已回滚: {}", formatTxnId(transaction.getTxnID()));
        } catch (Exception e) {
            log.error("回滚事务失败: {}", e.getMessage());
        }
    }

    /**
     * 查询事务状态
     * 用于故障恢复时检查未完成事务的实际状态
     */
    public String queryTransactionStatus(String txnIdString) {
        try {
            TxnID txnId = parseTxnId(txnIdString);
            if (txnId == null) {
                return TransactionRecord.STATUS_ABORTED;
            }

            // 尝试获取事务状态
            // 注意：Pulsar 客户端 API 可能不直接支持查询历史事务状态
            // 这里采用保守策略：如果无法确定状态，则认为需要重试
            log.info("查询事务状态: {}", txnIdString);

            // Pulsar 事务超时后会自动 abort
            // 由于无法直接查询历史事务状态，我们采用保守策略
            return null; // 返回 null 表示状态未知

        } catch (Exception e) {
            log.error("查询事务状态失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 格式化事务ID为字符串
     */
    public String formatTxnId(TxnID txnId) {
        return String.format("%d:%d", txnId.getMostSigBits(), txnId.getLeastSigBits());
    }

    /**
     * 解析事务ID字符串
     */
    private TxnID parseTxnId(String txnIdString) {
        try {
            String[] parts = txnIdString.split(":");
            if (parts.length == 2) {
                long mostSigBits = Long.parseLong(parts[0]);
                long leastSigBits = Long.parseLong(parts[1]);
                return new TxnID(mostSigBits, leastSigBits);
            }
        } catch (Exception e) {
            log.error("解析事务ID失败: {}", txnIdString);
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        try {
            if (producer != null) {
                producer.close();
            }
            if (pulsarClient != null) {
                pulsarClient.close();
            }
            log.info("Pulsar 资源已释放");
        } catch (PulsarClientException e) {
            throw new IOException("关闭 Pulsar 资源失败", e);
        }
    }
}
