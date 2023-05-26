package com.kuze.bigdata.study.sql;

import java.io.Serializable;
import java.util.List;

public class AverageList implements Serializable {
    private List<Long> list;

    public AverageList() {
    }

    public AverageList(List<Long> list) {
        this.list = list;
    }

    public List<Long> getList() {
        return list;
    }

    public void setList(List<Long> list) {
        this.list = list;
    }

}
