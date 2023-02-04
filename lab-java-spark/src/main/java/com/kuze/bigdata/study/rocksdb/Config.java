package com.kuze.bigdata.study.rocksdb;

import java.util.List;

public class Config{

    private List<String> configs;

    public Config(List<String> configs) {
        this.configs = configs;
    }

    public List<String> getConfigs() {
        return configs;
    }

    public void setConfigs(List<String> configs) {
        this.configs = configs;
    }
}
