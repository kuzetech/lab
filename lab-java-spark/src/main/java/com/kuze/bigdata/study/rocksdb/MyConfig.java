package com.kuze.bigdata.study.rocksdb;

import java.util.List;

public class MyConfig {

    private List<String> configs;

    public MyConfig(List<String> configs) {
        this.configs = configs;
    }

    public List<String> getConfigs() {
        return configs;
    }

    public void setConfigs(List<String> configs) {
        this.configs = configs;
    }
}
