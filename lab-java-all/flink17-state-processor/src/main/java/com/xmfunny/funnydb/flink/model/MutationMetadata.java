package com.xmfunny.funnydb.flink.model;

import java.io.Serializable;

/**
 * 变更操作元数据
 */
public interface MutationMetadata extends Serializable {
    String getDataLifecycle();

    String getIdentify();

    long getCreatedTime();
}
