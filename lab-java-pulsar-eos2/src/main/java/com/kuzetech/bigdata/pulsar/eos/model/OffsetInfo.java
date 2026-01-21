package com.kuzetech.bigdata.pulsar.eos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 偏移量信息
 * 记录当前已处理的文件偏移量
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffsetInfo {

    /**
     * 当前已确认的偏移量（行号）
     */
    private long committedOffset;

    /**
     * 输入文件路径
     */
    private String inputFile;

    /**
     * 最后更新时间
     */
    private long lastUpdateTime;
}
