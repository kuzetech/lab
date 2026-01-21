package com.kuzetech.bigdata.pulsar.eos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzetech.bigdata.pulsar.eos.model.OffsetInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 偏移量管理器
 * 负责偏移量的持久化和读取
 */
@Slf4j
public class OffsetManager {

    private final String offsetFilePath;
    private final String inputFilePath;
    private final ObjectMapper objectMapper;
    private OffsetInfo currentOffset;

    public OffsetManager(String offsetFilePath, String inputFilePath) {
        this.offsetFilePath = offsetFilePath;
        this.inputFilePath = inputFilePath;
        this.objectMapper = new ObjectMapper();
        this.currentOffset = loadOffset();
    }

    /**
     * 加载偏移量
     */
    private OffsetInfo loadOffset() {
        File file = new File(offsetFilePath);
        if (file.exists()) {
            try {
                OffsetInfo offset = objectMapper.readValue(file, OffsetInfo.class);
                // 验证是否是同一个文件
                if (inputFilePath.equals(offset.getInputFile())) {
                    log.info("加载已存在的偏移量: {}", offset.getCommittedOffset());
                    return offset;
                } else {
                    log.warn("偏移量文件对应的输入文件不匹配，将从头开始处理");
                }
            } catch (IOException e) {
                log.error("读取偏移量文件失败: {}", e.getMessage());
            }
        }
        // 返回初始偏移量
        return new OffsetInfo(0, inputFilePath, System.currentTimeMillis());
    }

    /**
     * 获取当前偏移量
     */
    public long getCurrentOffset() {
        return currentOffset.getCommittedOffset();
    }

    /**
     * 更新偏移量（内存中）
     */
    public void updateOffset(long newOffset) {
        currentOffset.setCommittedOffset(newOffset);
        currentOffset.setLastUpdateTime(System.currentTimeMillis());
    }

    /**
     * 持久化偏移量到文件
     */
    public void persistOffset() throws IOException {
        ensureDirectoryExists();
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(offsetFilePath), currentOffset);
        log.debug("偏移量已持久化: {}", currentOffset.getCommittedOffset());
    }

    /**
     * 回滚偏移量
     */
    public void rollbackOffset(long offset) throws IOException {
        currentOffset.setCommittedOffset(offset);
        currentOffset.setLastUpdateTime(System.currentTimeMillis());
        persistOffset();
        log.info("偏移量已回滚到: {}", offset);
    }

    /**
     * 确保目录存在
     */
    private void ensureDirectoryExists() throws IOException {
        Path path = Paths.get(offsetFilePath).getParent();
        if (path != null && !Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}
