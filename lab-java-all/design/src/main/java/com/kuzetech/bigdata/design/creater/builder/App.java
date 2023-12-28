package com.kuzetech.bigdata.design.creater.builder;

public class App {
    public static void main(String[] args) {
        ResourcePoolConfigBuilder builder = new ResourcePoolConfigBuilder();
        builder.setName("test");
        builder.setMaxIdle(10);
        ResourcePoolConfig config = builder.build();

    }
}
