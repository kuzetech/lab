package com.kuzetech.bigdata.pulsar.eos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 事务记录
 * 用于记录事务状态，支持故障恢复
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecord {

    /**
     * 事务ID (TxnID 的字符串表示)
     */
    private String transactionId;

    /**
     * 批次起始偏移量
     */
    private long startOffset;

    /**
     * 批次结束偏移量
     */
    private long endOffset;

    /**
     * 事务状态: PENDING, COMMITTED, ABORTED
     */
    private String status;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 更新时间
     */
    private long updateTime;

    // 事务状态常量
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMMITTED = "COMMITTED";
    public static final String STATUS_ABORTED = "ABORTED";
}
