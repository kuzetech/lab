package com.kuzetech.bigdata.study.streaming.udsink;

import com.kuzetech.bigdata.study.clickhouse.ClickHouseQueryConfig;

import java.io.IOException;
import java.io.Serializable;

public abstract class WalService implements Serializable {

    protected ClickHouseQueryConfig config;

    public WalService(ClickHouseQueryConfig config) {
        this.config = config;
    }


    public abstract Wal getWal() throws Exception;

    public abstract void setWal(Wal wal) throws Exception;

    public abstract void deleteWal() throws Exception;

    public abstract void closeConnect() throws IOException;

}
