package com.kuze.bigdata.study;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientLog {
    private String logId;
    private Boolean isImportant;

    private ClientLog() {

    }

    public static ClientLog getDefaultInstance() {
        ClientLog log = new ClientLog();
        log.setLogId("1");
        log.setIsImportant(true);
        return log;
    }
}
