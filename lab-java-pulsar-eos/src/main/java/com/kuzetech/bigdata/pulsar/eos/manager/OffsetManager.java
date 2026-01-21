package com.kuzetech.bigdata.pulsar.eos.manager;

import com.kuzetech.bigdata.pulsar.eos.config.AppConfig;
import com.kuzetech.bigdata.pulsar.eos.entity.FileOffset;
import com.kuzetech.bigdata.pulsar.eos.entity.TransactionLog;
import com.kuzetech.bigdata.pulsar.eos.mapper.FileOffsetMapper;
import com.kuzetech.bigdata.pulsar.eos.mapper.TransactionLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Slf4j
public class OffsetManager implements AutoCloseable {
    private final SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;

    public OffsetManager(AppConfig.MysqlConfig config) throws IOException {
        log.info("Initializing MyBatis with MySQL: {}", config.getJdbcUrl());

        // 读取 MyBatis 配置
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);

        // 设置数据库连接属性
        Properties properties = new Properties();
        properties.setProperty("jdbc.url", config.getJdbcUrl());
        properties.setProperty("jdbc.username", config.getUsername());
        properties.setProperty("jdbc.password", config.getPassword());

        // 构建 SqlSessionFactory
        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, properties);
        this.sqlSession = sqlSessionFactory.openSession(false); // 关闭自动提交，支持事务

        log.info("MyBatis initialized successfully");
    }

    /**
     * 开始新的 SQL 事务
     */
    public void beginTransaction() {
        // MyBatis 默认就是事务模式，这里只是语义上的标记
        log.debug("MySQL transaction started");
    }

    /**
     * 提交 SQL 事务
     */
    public void commit() {
        sqlSession.commit();
        log.debug("MySQL transaction committed");
    }

    /**
     * 回滚 SQL 事务
     */
    public void rollback() {
        sqlSession.rollback();
        log.debug("MySQL transaction rolled back");
    }

    public FileOffset getOffset(String filePath, long fileSize) {
        FileOffsetMapper mapper = sqlSession.getMapper(FileOffsetMapper.class);
        FileOffset offset = mapper.selectByFilePath(filePath);

        if (offset != null) {
            long storedFileSize = offset.getFileSize();
            if (storedFileSize != fileSize) {
                log.warn("File size changed! Stored: {}, Current: {}. Starting from beginning.",
                        storedFileSize, fileSize);
                return FileOffset.builder()
                        .filePath(filePath)
                        .fileSize(fileSize)
                        .offsetPosition(0L)
                        .totalLines(0L)
                        .processedLines(0L)
                        .status("PROCESSING")
                        .build();
            }
            return offset;
        }

        return FileOffset.builder()
                .filePath(filePath)
                .fileSize(fileSize)
                .offsetPosition(0L)
                .totalLines(0L)
                .processedLines(0L)
                .status("PROCESSING")
                .build();
    }

    public void initializeOffset(String filePath, long fileSize) {
        FileOffsetMapper mapper = sqlSession.getMapper(FileOffsetMapper.class);
        FileOffset fileOffset = FileOffset.builder()
                .filePath(filePath)
                .fileSize(fileSize)
                .offsetPosition(0L)
                .totalLines(0L)
                .processedLines(0L)
                .status("PROCESSING")
                .build();

        mapper.insertOrUpdate(fileOffset);
        sqlSession.commit(); // 立即提交
        log.info("Initialized offset for file: {}", filePath);
    }

    public void updateOffset(String filePath, long offsetPosition, long processedLines) {
        FileOffsetMapper mapper = sqlSession.getMapper(FileOffsetMapper.class);
        int updated = mapper.updateOffset(filePath, offsetPosition, processedLines);

        if (updated == 0) {
            log.warn("No offset record updated for file: {}", filePath);
        }
        // 注意：不在这里提交，由调用方控制事务
    }

    public void markCompleted(String filePath, long totalLines) {
        FileOffsetMapper mapper = sqlSession.getMapper(FileOffsetMapper.class);
        mapper.markCompleted(filePath, totalLines);
        log.info("Marked file as completed: {}", filePath);
    }

    public void markFailed(String filePath, String errorMessage) {
        FileOffsetMapper mapper = sqlSession.getMapper(FileOffsetMapper.class);
        mapper.markFailed(filePath, errorMessage);
        sqlSession.commit(); // 立即提交
        log.info("Marked file as failed: {}", filePath);
    }

    /**
     * 记录事务状态
     */
    public void logTransaction(String filePath, String transactionId, long startOffset,
                                long endOffset, int messageCount, TransactionLog.TransactionStatus status) {
        TransactionLogMapper mapper = sqlSession.getMapper(TransactionLogMapper.class);
        TransactionLog transactionLog = TransactionLog.builder()
                .filePath(filePath)
                .transactionId(transactionId)
                .startOffset(startOffset)
                .endOffset(endOffset)
                .messageCount(messageCount)
                .status(status)
                .build();
        mapper.insert(transactionLog);
        // 注意：不在这里提交，由调用方控制事务
    }

    /**
     * 更新事务状态
     */
    public void updateTransactionStatus(String transactionId, TransactionLog.TransactionStatus status) {
        TransactionLogMapper mapper = sqlSession.getMapper(TransactionLogMapper.class);
        mapper.updateStatus(transactionId, status);
        // 注意：不在这里提交，由调用方控制事务
    }

    /**
     * 查询待提交的事务（PREPARED 状态）
     */
    public List<TransactionLog> getPreparedTransactions(String filePath) {
        TransactionLogMapper mapper = sqlSession.getMapper(TransactionLogMapper.class);
        return mapper.selectPreparedByFilePath(filePath);
    }

    /**
     * 根据事务ID查询事务记录
     */
    public TransactionLog getTransactionById(String transactionId) {
        TransactionLogMapper mapper = sqlSession.getMapper(TransactionLogMapper.class);
        return mapper.selectByTransactionId(transactionId);
    }

    @Override
    public void close() {
        if (sqlSession != null) {
            sqlSession.close();
            log.info("MyBatis SqlSession closed");
        }
    }
}
