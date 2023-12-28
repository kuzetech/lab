package com.kuzetech.bigdata.design.creater.builder;

import org.apache.commons.lang3.StringUtils;

public class ResourcePoolConfigBuilder {

    private static final int DEFAULT_MAX_TOTAL = 8;
    private static final int DEFAULT_MAX_IDLE = 8;
    private static final int DEFAULT_MIN_IDLE = 0;

    private String name;
    private int maxTotal = DEFAULT_MAX_TOTAL;
    private int maxIdle = DEFAULT_MAX_IDLE;
    private int minIdle = DEFAULT_MIN_IDLE;

    public ResourcePoolConfigBuilder() {
    }

    public ResourcePoolConfig build() {
        checkConfig();
        return new ResourcePoolConfig(this);
    }

    private void checkConfig() {
        // 校验逻辑放到这里来做，包括必填项校验、依赖关系校验、约束条件校验等
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("...");
        }
        if (maxIdle > maxTotal) {
            throw new IllegalArgumentException("...");
        }
        if (minIdle > maxTotal || minIdle > maxIdle) {
            throw new IllegalArgumentException("...");
        }
    }


    public ResourcePoolConfigBuilder setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("...");
        }
        this.name = name;
        return this;
    }

    public ResourcePoolConfigBuilder setMaxTotal(int maxTotal) {
        if (maxTotal <= 0) {
            throw new IllegalArgumentException("...");
        }
        this.maxTotal = maxTotal;
        return this;
    }

    public ResourcePoolConfigBuilder setMaxIdle(int maxIdle) {
        if (maxIdle < 0) {
            throw new IllegalArgumentException("...");
        }
        this.maxIdle = maxIdle;
        return this;
    }

    public ResourcePoolConfigBuilder setMinIdle(int minIdle) {
        if (minIdle < 0) {
            throw new IllegalArgumentException("...");
        }
        this.minIdle = minIdle;
        return this;
    }

    public String getName() {
        return name;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }
}
