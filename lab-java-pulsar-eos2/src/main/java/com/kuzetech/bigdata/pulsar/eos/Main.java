package com.kuzetech.bigdata.pulsar.eos;

import com.kuzetech.bigdata.pulsar.eos.config.AppConfig;
import com.kuzetech.bigdata.pulsar.eos.model.TransactionRecord;
import com.kuzetech.bigdata.pulsar.eos.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.transaction.Transaction;

import java.io.IOException;
import java.util.List;

/**
 * EOS (Exactly Once Semantics) 主程序
 * 读取日志文件并通过事务发送到 Pulsar，保证数据不丢失不重复
 */
@Slf4j
public class Main {

    private final AppConfig config;
    private final FileReaderService fileReader;
    private final OffsetManager offsetManager;
    private final TransactionLogManager transactionLogManager;
    private PulsarProducerService pulsarProducer;

    public Main(AppConfig config) {
        this.config = config;
        this.fileReader = new FileReaderService(config.getInputFilePath(), config.getMaxFileSize());
        this.offsetManager = new OffsetManager(config.getOffsetFilePath(), config.getInputFilePath());
        this.transactionLogManager = new TransactionLogManager(config.getTransactionLogPath());
    }

    /**
     * 运行主程序
     */
    public void run() {
        try {
            // 验证输入文件
            fileReader.validateFile();

            // 初始化 Pulsar Producer
            pulsarProducer = new PulsarProducerService(config);

            // 恢复未完成的事务
            recoverPendingTransactions();

            // 处理文件
            processFile();

            log.info("文件处理完成！");

        } catch (Exception e) {
            log.error("程序执行失败: {}", e.getMessage(), e);
            System.exit(1);
        } finally {
            cleanup();
        }
    }

    /**
     * 恢复未完成的事务
     */
    private void recoverPendingTransactions() throws IOException {
        List<TransactionRecord> pendingTransactions = transactionLogManager.getPendingTransactions();

        if (pendingTransactions.isEmpty()) {
            log.info("没有需要恢复的未完成事务");
            return;
        }

        log.info("发现 {} 个未完成的事务，开始恢复...", pendingTransactions.size());

        for (TransactionRecord record : pendingTransactions) {
            log.info("检查事务: txnId={}, startOffset={}, endOffset={}",
                    record.getTransactionId(), record.getStartOffset(), record.getEndOffset());

            // 查询事务状态
            String actualStatus = pulsarProducer.queryTransactionStatus(record.getTransactionId());

            if (TransactionRecord.STATUS_COMMITTED.equals(actualStatus)) {
                // 事务已提交，更新本地状态
                transactionLogManager.updateTransactionStatus(
                        record.getTransactionId(), TransactionRecord.STATUS_COMMITTED);
                offsetManager.updateOffset(record.getEndOffset());
                offsetManager.persistOffset();
                log.info("事务 {} 已确认提交，偏移量更新到 {}",
                        record.getTransactionId(), record.getEndOffset());

            } else if (TransactionRecord.STATUS_ABORTED.equals(actualStatus)) {
                // 事务已回滚，回滚本地偏移量
                transactionLogManager.updateTransactionStatus(
                        record.getTransactionId(), TransactionRecord.STATUS_ABORTED);
                offsetManager.rollbackOffset(record.getStartOffset());
                log.info("事务 {} 已回滚，偏移量回滚到 {}",
                        record.getTransactionId(), record.getStartOffset());

            } else {
                // 状态未知，保守处理：回滚到事务开始前的偏移量
                log.warn("事务 {} 状态未知，保守处理：回滚偏移量", record.getTransactionId());
                transactionLogManager.updateTransactionStatus(
                        record.getTransactionId(), TransactionRecord.STATUS_ABORTED);
                offsetManager.rollbackOffset(record.getStartOffset());
            }
        }

        log.info("事务恢复完成");
    }

    /**
     * 处理文件
     */
    private void processFile() throws Exception {
        long currentOffset = offsetManager.getCurrentOffset();
        long totalLines = fileReader.getTotalLines();

        log.info("开始处理文件，当前偏移量: {}, 总行数: {}", currentOffset, totalLines);

        while (currentOffset < totalLines) {
            // 读取一批数据
            List<String> batch = fileReader.readBatch(currentOffset, config.getBatchSize());

            if (batch.isEmpty()) {
                break;
            }

            long startOffset = currentOffset;
            long endOffset = currentOffset + batch.size();

            // 发送批次数据（带重试）
            boolean success = sendBatchWithRetry(batch, startOffset, endOffset);

            if (success) {
                currentOffset = endOffset;
                log.info("批次处理完成，新偏移量: {}/{}", currentOffset, totalLines);
            } else {
                log.error("批次发送失败，停止处理");
                break;
            }
        }

        // 清理旧的事务记录
        transactionLogManager.cleanupOldRecords(100);
    }

    /**
     * 发送批次数据（带重试）
     */
    private boolean sendBatchWithRetry(List<String> batch, long startOffset, long endOffset) {
        int attempt = 0;

        while (attempt < config.getMaxRetryAttempts()) {
            attempt++;
            Transaction transaction = null;

            try {
                // 1. 创建事务
                transaction = pulsarProducer.createTransaction();
                String txnId = pulsarProducer.formatTxnId(transaction.getTxnID());

                // 2. 记录事务开始（PENDING 状态）
                transactionLogManager.logTransactionStart(txnId, startOffset, endOffset);

                // 3. 发送消息到 Pulsar
                pulsarProducer.sendBatchWithTransaction(batch, transaction);

                // 4. 提交事务
                pulsarProducer.commitTransaction(transaction);

                // 5. 更新事务状态为 COMMITTED
                transactionLogManager.updateTransactionStatus(txnId, TransactionRecord.STATUS_COMMITTED);

                // 6. 更新并持久化偏移量
                offsetManager.updateOffset(endOffset);
                offsetManager.persistOffset();

                log.info("批次发送成功: startOffset={}, endOffset={}, txnId={}",
                        startOffset, endOffset, txnId);
                return true;

            } catch (Exception e) {
                log.error("发送批次失败 (尝试 {}/{}): {}",
                        attempt, config.getMaxRetryAttempts(), e.getMessage());

                // 回滚事务
                if (transaction != null) {
                    pulsarProducer.abortTransaction(transaction);
                }

                // 等待后重试
                if (attempt < config.getMaxRetryAttempts()) {
                    try {
                        Thread.sleep(config.getRetryIntervalMs());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 清理资源
     */
    private void cleanup() {
        try {
            if (pulsarProducer != null) {
                pulsarProducer.close();
            }
        } catch (IOException e) {
            log.error("清理资源失败: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        log.info("=== Pulsar EOS 程序启动 ===");

        // 加载配置
        AppConfig config = AppConfig.load(args);

        // 运行程序
        Main app = new Main(config);
        app.run();

        log.info("=== Pulsar EOS 程序结束 ===");
    }
}