package com.kuzetech.bigdata.pulsar.eos.service;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件读取服务
 * 负责读取日志文件内容，支持从指定偏移量开始读取
 */
@Slf4j
public class FileReaderService {

    private final String filePath;
    private final long maxFileSize;

    public FileReaderService(String filePath, long maxFileSize) {
        this.filePath = filePath;
        this.maxFileSize = maxFileSize;
    }

    /**
     * 验证文件
     */
    public void validateFile() throws IOException {
        Path path = Paths.get(filePath);

        // 检查文件是否存在
        if (!Files.exists(path)) {
            throw new IOException("文件不存在: " + filePath);
        }

        // 检查文件扩展名
        if (!filePath.endsWith(".log")) {
            throw new IOException("文件格式不正确，必须是 .log 文件: " + filePath);
        }

        // 检查文件大小
        long fileSize = Files.size(path);
        if (fileSize > maxFileSize) {
            throw new IOException(String.format(
                    "文件大小超过限制: %d bytes > %d bytes", fileSize, maxFileSize));
        }

        log.info("文件验证通过: {}, 大小: {} bytes", filePath, fileSize);
    }

    /**
     * 从指定偏移量读取一批数据
     *
     * @param startOffset 起始行号（从0开始）
     * @param batchSize 批次大小
     * @return 读取的行数据
     */
    public List<String> readBatch(long startOffset, int batchSize) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            long currentLine = 0;

            // 跳过已处理的行
            while (currentLine < startOffset && (line = reader.readLine()) != null) {
                currentLine++;
            }

            // 读取批次数据
            while (lines.size() < batchSize && (line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        log.debug("从偏移量 {} 读取了 {} 行数据", startOffset, lines.size());
        return lines;
    }

    /**
     * 获取文件总行数
     */
    public long getTotalLines() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            long count = 0;
            while (reader.readLine() != null) {
                count++;
            }
            return count;
        }
    }

    /**
     * 检查是否还有更多数据
     */
    public boolean hasMoreData(long currentOffset) throws IOException {
        return currentOffset < getTotalLines();
    }
}
