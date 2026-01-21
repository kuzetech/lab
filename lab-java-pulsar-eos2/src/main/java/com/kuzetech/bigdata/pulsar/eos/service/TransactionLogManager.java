package com.kuzetech.bigdata.pulsar.eos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzetech.bigdata.pulsar.eos.model.TransactionRecord;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 事务日志管理器
 * 负责事务记录的持久化和读取，支持故障恢复
 */
@Slf4j
public class TransactionLogManager {

    private final String transactionLogPath;
    private final ObjectMapper objectMapper;

    public TransactionLogManager(String transactionLogPath) {
        this.transactionLogPath = transactionLogPath;
        this.objectMapper = new ObjectMapper();
        ensureDirectoryExists();
    }

    /**
     * 记录事务开始（PENDING 状态）
     */
    public void logTransactionStart(String transactionId, long startOffset, long endOffset) throws IOException {
        TransactionRecord record = new TransactionRecord(
                transactionId,
                startOffset,
                endOffset,
                TransactionRecord.STATUS_PENDING,
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );
        appendRecord(record);
        log.debug("记录事务开始: txnId={}, startOffset={}, endOffset={}",
                transactionId, startOffset, endOffset);
    }

    /**
     * 更新事务状态
     */
    public void updateTransactionStatus(String transactionId, String status) throws IOException {
        List<TransactionRecord> records = loadAllRecords();
        boolean found = false;

        for (TransactionRecord record : records) {
            if (record.getTransactionId().equals(transactionId)) {
                record.setStatus(status);
                record.setUpdateTime(System.currentTimeMillis());
                found = true;
                break;
            }
        }

        if (found) {
            saveAllRecords(records);
            log.debug("更新事务状态: txnId={}, status={}", transactionId, status);
        } else {
            log.warn("未找到事务记录: txnId={}", transactionId);
        }
    }

    /**
     * 获取所有未完成（PENDING）的事务
     */
    public List<TransactionRecord> getPendingTransactions() {
        List<TransactionRecord> records = loadAllRecords();
        List<TransactionRecord> pendingRecords = new ArrayList<>();

        for (TransactionRecord record : records) {
            if (TransactionRecord.STATUS_PENDING.equals(record.getStatus())) {
                pendingRecords.add(record);
            }
        }

        return pendingRecords;
    }

    /**
     * 获取最新的已提交事务
     */
    public TransactionRecord getLatestCommittedTransaction() {
        List<TransactionRecord> records = loadAllRecords();
        TransactionRecord latest = null;

        for (TransactionRecord record : records) {
            if (TransactionRecord.STATUS_COMMITTED.equals(record.getStatus())) {
                if (latest == null || record.getEndOffset() > latest.getEndOffset()) {
                    latest = record;
                }
            }
        }

        return latest;
    }

    /**
     * 加载所有事务记录
     */
    private List<TransactionRecord> loadAllRecords() {
        List<TransactionRecord> records = new ArrayList<>();
        File file = new File(transactionLogPath);

        if (!file.exists()) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    TransactionRecord record = objectMapper.readValue(line, TransactionRecord.class);
                    records.add(record);
                }
            }
        } catch (IOException e) {
            log.error("读取事务日志失败: {}", e.getMessage());
        }

        return records;
    }

    /**
     * 追加事务记录
     */
    private void appendRecord(TransactionRecord record) throws IOException {
        try (FileWriter writer = new FileWriter(transactionLogPath, true)) {
            writer.write(objectMapper.writeValueAsString(record));
            writer.write("\n");
        }
    }

    /**
     * 保存所有事务记录
     */
    private void saveAllRecords(List<TransactionRecord> records) throws IOException {
        try (FileWriter writer = new FileWriter(transactionLogPath, false)) {
            for (TransactionRecord record : records) {
                writer.write(objectMapper.writeValueAsString(record));
                writer.write("\n");
            }
        }
    }

    /**
     * 清理已完成的事务记录（保留最近的 N 条）
     */
    public void cleanupOldRecords(int keepCount) throws IOException {
        List<TransactionRecord> records = loadAllRecords();

        // 只保留 COMMITTED 状态的最近 N 条和所有 PENDING 状态的记录
        List<TransactionRecord> toKeep = new ArrayList<>();
        List<TransactionRecord> committed = new ArrayList<>();

        for (TransactionRecord record : records) {
            if (TransactionRecord.STATUS_PENDING.equals(record.getStatus())) {
                toKeep.add(record);
            } else if (TransactionRecord.STATUS_COMMITTED.equals(record.getStatus())) {
                committed.add(record);
            }
        }

        // 按更新时间倒序，保留最近的
        committed.sort((a, b) -> Long.compare(b.getUpdateTime(), a.getUpdateTime()));
        int count = Math.min(keepCount, committed.size());
        toKeep.addAll(committed.subList(0, count));

        saveAllRecords(toKeep);
        log.debug("清理旧事务记录，保留 {} 条", toKeep.size());
    }

    /**
     * 确保目录存在
     */
    private void ensureDirectoryExists() {
        try {
            Path path = Paths.get(transactionLogPath).getParent();
            if (path != null && !Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            log.error("创建事务日志目录失败: {}", e.getMessage());
        }
    }
}
