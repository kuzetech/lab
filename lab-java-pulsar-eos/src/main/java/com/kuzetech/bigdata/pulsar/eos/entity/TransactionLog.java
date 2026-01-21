package com.kuzetech.bigdata.pulsar.eos.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLog {
    private Long id;
    private String filePath;
    private String transactionId;
    private Long startOffset;
    private Long endOffset;
    private Integer messageCount;
    private TransactionStatus status;
    private Date createdAt;
    private Date updatedAt;

    public enum TransactionStatus {
        PREPARED,   // offset 已更新，Pulsar 事务待提交
        COMMITTED,  // Pulsar 事务已提交成功
        ABORTED,    // Pulsar 事务已中止
        UNKNOWN     // 事务状态未知（需要查询 broker）
    }
}
