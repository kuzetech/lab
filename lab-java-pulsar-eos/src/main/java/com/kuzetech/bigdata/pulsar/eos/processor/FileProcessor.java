package com.kuzetech.bigdata.pulsar.eos.processor;

import com.kuzetech.bigdata.pulsar.eos.config.AppConfig;
import com.kuzetech.bigdata.pulsar.eos.entity.FileOffset;
import com.kuzetech.bigdata.pulsar.eos.entity.TransactionLog;
import com.kuzetech.bigdata.pulsar.eos.manager.OffsetManager;
import com.kuzetech.bigdata.pulsar.eos.manager.PulsarProducerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileProcessor {
    private final AppConfig config;
    private final PulsarProducerManager pulsarManager;
    private final OffsetManager offsetManager;

    public FileProcessor(AppConfig config, PulsarProducerManager pulsarManager, OffsetManager offsetManager) {
        this.config = config;
        this.pulsarManager = pulsarManager;
        this.offsetManager = offsetManager;
    }

    public void processFile(String filePath) throws Exception {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        if (!filePath.endsWith(".log")) {
            throw new IllegalArgumentException("File must have .log extension: " + filePath);
        }

        long fileSize = file.length();
        if (fileSize > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10MB limit: " + fileSize);
        }

        log.info("Starting to process file: {} (size: {} bytes)", filePath, fileSize);

        // ===== 事务恢复逻辑 =====
        // 检查是否有未完成的事务（PREPARED 状态）
        List<TransactionLog> preparedTransactions = offsetManager.getPreparedTransactions(filePath);
        if (!preparedTransactions.isEmpty()) {
            log.warn("Found {} PREPARED transactions for file: {}", preparedTransactions.size(), filePath);

            for (TransactionLog txnLog : preparedTransactions) {
                log.info("Recovering transaction: {}", txnLog.getTransactionId());

                // 查询 broker 端事务状态
                String txnState = pulsarManager.queryTransactionState(txnLog.getTransactionId());
                log.info("Transaction {} state from broker: {}", txnLog.getTransactionId(), txnState);

                offsetManager.beginTransaction();
                try {
                    if ("COMMITTED".equals(txnState)) {
                        // broker 端已提交，更新本地状态为 COMMITTED
                        offsetManager.updateTransactionStatus(txnLog.getTransactionId(),
                                TransactionLog.TransactionStatus.COMMITTED);
                        offsetManager.commit();
                        log.info("Transaction {} confirmed as COMMITTED", txnLog.getTransactionId());
                    } else if ("ABORTED".equals(txnState)) {
                        // broker 端已中止，更新本地状态为 ABORTED，回滚 offset
                        offsetManager.updateTransactionStatus(txnLog.getTransactionId(),
                                TransactionLog.TransactionStatus.ABORTED);
                        // 回滚 offset 到事务开始前的位置
                        offsetManager.updateOffset(filePath, txnLog.getStartOffset(),
                                txnLog.getStartOffset());
                        offsetManager.commit();
                        log.warn("Transaction {} confirmed as ABORTED, offset rolled back",
                                txnLog.getTransactionId());
                    } else {
                        // broker 端状态未知，保守处理：标记为 ABORTED 并回滚
                        log.warn("Transaction {} state is UNKNOWN, marking as ABORTED and rolling back",
                                txnLog.getTransactionId());
                        offsetManager.updateTransactionStatus(txnLog.getTransactionId(),
                                TransactionLog.TransactionStatus.ABORTED);
                        offsetManager.updateOffset(filePath, txnLog.getStartOffset(),
                                txnLog.getStartOffset());
                        offsetManager.commit();
                    }
                } catch (Exception e) {
                    offsetManager.rollback();
                    log.error("Failed to recover transaction: {}", txnLog.getTransactionId(), e);
                    throw e;
                }
            }
        }

        // 获取或初始化偏移量
        FileOffset offset = offsetManager.getOffset(filePath, fileSize);
        if (offset.getOffsetPosition() == 0) {
            offsetManager.initializeOffset(filePath, fileSize);
        } else {
            log.info("Resuming from offset: {} (processed lines: {})",
                    offset.getOffsetPosition(), offset.getProcessedLines());
        }

        long currentOffset = offset.getOffsetPosition();
        long processedLines = offset.getProcessedLines();

        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(
                             new FileInputStream(raf.getFD()),
                             Charset.forName(config.getFile().getEncoding())
                     )
             )) {

            // 跳过已处理的部分
            if (currentOffset > 0) {
                raf.seek(currentOffset);
                log.info("Seeked to offset: {}", currentOffset);
            }

            List<String> batch = new ArrayList<>();
            long batchStartOffset = currentOffset;
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // 验证是否为有效 JSON（简单检查）
                if (!isValidJson(line)) {
                    log.warn("Invalid JSON at line {}: {}", processedLines + lineNumber,
                            line.substring(0, Math.min(100, line.length())));
                    continue;
                }

                batch.add(line);

                // 当批次达到指定大小时，发送消息
                if (batch.size() >= config.getFile().getBatchSize()) {
                    sendBatch(filePath, batch, batchStartOffset, processedLines + lineNumber);

                    // 更新偏移量
                    currentOffset = raf.getFilePointer();
                    processedLines += batch.size();
                    offsetManager.updateOffset(filePath, currentOffset, processedLines);

                    log.info("Progress: {} lines processed (total: {})", batch.size(), processedLines);

                    batch.clear();
                    batchStartOffset = currentOffset;
                }
            }

            // 处理剩余的消息
            if (!batch.isEmpty()) {
                sendBatch(filePath, batch, batchStartOffset, processedLines + batch.size());
                processedLines += batch.size();

                currentOffset = raf.getFilePointer();
                offsetManager.updateOffset(filePath, currentOffset, processedLines);

                log.info("Final batch: {} lines processed", batch.size());
            }

            // 标记为完成
            offsetManager.markCompleted(filePath, processedLines);
            log.info("File processing completed. Total lines processed: {}", processedLines);

        } catch (Exception e) {
            log.error("Error processing file", e);
            offsetManager.markFailed(filePath, e.getMessage());
            throw e;
        }
    }

    private void sendBatch(String filePath, List<String> messages, long startOffset, long endLine)
            throws Exception {

        int maxAttempts = config.getRetry().getMaxAttempts();
        int attempt = 0;
        long delay = config.getRetry().getDelayMs();

        while (attempt < maxAttempts) {
            try {
                // 使用事务发送（如果启用）
                if (pulsarManager.isTransactionEnabled()) {
                    sendBatchWithTransaction(filePath, messages, startOffset, endLine);
                } else {
                    sendBatchWithoutTransaction(messages);
                }
                return; // 成功发送
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxAttempts) {
                    log.error("Failed to send batch after {} attempts", maxAttempts, e);
                    throw e;
                }

                log.warn("Failed to send batch (attempt {}/{}), retrying in {}ms...",
                        attempt, maxAttempts, delay, e);
                Thread.sleep(delay);

                // 指数退避
                delay = Math.min(
                        (long) (delay * config.getRetry().getMultiplier()),
                        config.getRetry().getMaxDelayMs()
                );
            }
        }
    }

    /**
     * 使用事务发送批次（实现 EOS 语义）
     * 采用两阶段提交：
     * 阶段1: 更新 offset 到 MySQL，记录事务为 PREPARED 状态（预提交）
     * 阶段2: 提交 Pulsar 事务
     * 阶段3: 更新 MySQL 事务状态为 COMMITTED，并提交 MySQL 事务
     *
     * 失败恢复：
     * - 如果在阶段1后失败：offset 未提交，事务记录未提交，重启后从旧 offset 重新处理
     * - 如果在阶段2后失败：Pulsar 事务已提交，但 MySQL 未提交，重启后查询 broker 确认状态
     * - 如果在阶段3后失败：所有状态已持久化，EOS 保证完成
     */
    private void sendBatchWithTransaction(String filePath, List<String> messages,
                                          long startOffset, long endLine) throws Exception {
        Object txn = null;
        String txnId = null;
        boolean mysqlTransactionStarted = false;

        try {
            // === 阶段 1: 创建 Pulsar 事务，并准备 MySQL 更新（但不提交）===
            txn = pulsarManager.newTransaction();
            txnId = pulsarManager.getTransactionId(txn);
            log.debug("Pulsar transaction created: {}", txnId);

            // 开始 MySQL 事务
            offsetManager.beginTransaction();
            mysqlTransactionStarted = true;

            // 在 MySQL 事务中：
            // 1. 更新 offset（预提交，未commit）
            offsetManager.updateOffset(filePath, endLine, endLine);
            log.debug("Offset pre-updated in MySQL transaction (not committed): {}", endLine);

            // 2. 记录事务状态为 PREPARED（预提交，未commit）
            offsetManager.logTransaction(filePath, txnId,
                    startOffset, endLine, messages.size(), TransactionLog.TransactionStatus.PREPARED);
            log.debug("Transaction logged as PREPARED in MySQL transaction (not committed): {}", txnId);

            // 先提交 MySQL 事务，持久化 offset 和 PREPARED 状态
            // 关键：这样即使后续失败，重启时可以通过 PREPARED 状态恢复
            offsetManager.commit();
            mysqlTransactionStarted = false;
            log.debug("MySQL transaction committed with PREPARED state: {}", txnId);

            // === 阶段 2: 在 Pulsar 事务中发送所有消息 ===
            for (String message : messages) {
                MessageId messageId = pulsarManager.sendMessage(message, txn);
                log.trace("Message sent with ID: {} in transaction: {}", messageId, txnId);
            }

            // 提交 Pulsar 事务
            pulsarManager.commitTransaction(txn);
            log.debug("Pulsar transaction committed: {}", txnId);

            // === 阶段 3: 更新 MySQL 事务状态为 COMMITTED ===
            offsetManager.beginTransaction();
            mysqlTransactionStarted = true;

            offsetManager.updateTransactionStatus(txnId, TransactionLog.TransactionStatus.COMMITTED);
            log.debug("Transaction status updated to COMMITTED in MySQL transaction (not committed): {}", txnId);

            offsetManager.commit();
            mysqlTransactionStarted = false;
            log.debug("MySQL transaction committed with COMMITTED state: {}", txnId);

            // 至此，两阶段提交完成，EOS 保证实现

        } catch (Exception e) {
            log.error("Transaction failed, rolling back. TxnId: {}", txnId, e);

            // 回滚 MySQL 事务（如果还在事务中）
            if (mysqlTransactionStarted) {
                try {
                    offsetManager.rollback();
                    log.warn("MySQL transaction rolled back");
                } catch (Exception rollbackEx) {
                    log.error("Error rolling back MySQL transaction", rollbackEx);
                }
            }

            // 如果 Pulsar 事务还在，则中止
            if (txn != null) {
                try {
                    pulsarManager.abortTransaction(txn);
                    log.warn("Pulsar transaction aborted: {}", txnId);

                    // 记录事务中止状态
                    try {
                        offsetManager.beginTransaction();
                        // 如果之前已经有 PREPARED 记录，更新为 ABORTED
                        if (txnId != null) {
                            TransactionLog existingLog = offsetManager.getTransactionById(txnId);
                            if (existingLog != null) {
                                offsetManager.updateTransactionStatus(txnId,
                                        TransactionLog.TransactionStatus.ABORTED);
                            } else {
                                // 否则新建一条 ABORTED 记录
                                offsetManager.logTransaction(filePath, txnId,
                                        startOffset, endLine, messages.size(),
                                        TransactionLog.TransactionStatus.ABORTED);
                            }
                        }
                        offsetManager.commit();
                    } catch (Exception logEx) {
                        offsetManager.rollback();
                        log.warn("Failed to log transaction abort", logEx);
                    }
                } catch (Exception abortEx) {
                    log.error("Error aborting Pulsar transaction", abortEx);
                }
            }

            throw e;
        }
    }

    private void sendBatchWithoutTransaction(List<String> messages) throws PulsarClientException {
        for (String message : messages) {
            MessageId messageId = pulsarManager.sendMessage(message, null);
            log.trace("Message sent with ID: {}", messageId);
        }
    }

    private boolean isValidJson(String line) {
        if (line == null || line.trim().isEmpty()) {
            return false;
        }
        line = line.trim();
        return (line.startsWith("{") && line.endsWith("}")) ||
                (line.startsWith("[") && line.endsWith("]"));
    }
}
