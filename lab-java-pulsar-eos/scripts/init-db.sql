-- 创建偏移量表
CREATE TABLE IF NOT EXISTS file_offsets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_path VARCHAR(512) NOT NULL UNIQUE,
    file_size BIGINT NOT NULL,
    offset_position BIGINT NOT NULL DEFAULT 0,
    total_lines BIGINT NOT NULL DEFAULT 0,
    processed_lines BIGINT NOT NULL DEFAULT 0,
    status ENUM('PROCESSING', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'PROCESSING',
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_file_path (file_path),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建事务记录表（用于审计和追踪）
CREATE TABLE IF NOT EXISTS transaction_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_path VARCHAR(512) NOT NULL,
    transaction_id VARCHAR(128) NOT NULL,
    start_offset BIGINT NOT NULL,
    end_offset BIGINT NOT NULL,
    message_count INT NOT NULL,
    -- 状态说明：
    -- PREPARED: offset 已更新，Pulsar 事务待提交
    -- COMMITTED: Pulsar 事务已提交成功
    -- ABORTED: Pulsar 事务已中止
    -- UNKNOWN: 事务状态未知（需要查询 broker）
    status ENUM('PREPARED', 'COMMITTED', 'ABORTED', 'UNKNOWN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_file_path (file_path),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_transaction_id (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
