package com.kuzetech.bigdata.study.funnel;

import java.io.Serializable;
import java.util.List;

public class EventList implements Serializable {

    private List<Event> list;

    public EventList() {
    }

    public EventList(List<Event> list) {
        this.list = list;
    }

    public List<Event> getList() {
        return list;
    }

    public void setList(List<Event> list) {
        this.list = list;
    }

}
