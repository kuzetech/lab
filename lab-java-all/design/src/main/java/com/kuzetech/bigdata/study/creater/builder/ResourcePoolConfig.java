package com.kuzetech.bigdata.study.creater.builder;

public class ResourcePoolConfig {

    private String name;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;

    protected ResourcePoolConfig(ResourcePoolConfigBuilder builder) {
        this.name = builder.getName();
        this.maxTotal = builder.getMaxTotal();
        this.maxIdle = builder.getMaxIdle();
        this.minIdle = builder.getMinIdle();
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
