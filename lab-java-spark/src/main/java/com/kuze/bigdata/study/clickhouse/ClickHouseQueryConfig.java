package com.kuze.bigdata.study.clickhouse;

import java.io.Serializable;

public class ClickHouseQueryConfig implements Serializable {

    private String connectUrl;
    private String cluster;
    private String port;
    private String user;
    private String password;
    private String database;
    private String table;
    private String shardingColumn;

    public ClickHouseQueryConfig(
            String connectUrl, String cluster, String port,
            String user, String password, String database,
            String table, String shardingColumn) {
        this.connectUrl = connectUrl;
        this.cluster = cluster;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
        this.table = table;
        this.shardingColumn = shardingColumn;
    }

    @Override
    public String toString() {
        return "ClickHouseQueryConfig{" +
                "connectUrl='" + connectUrl + '\'' +
                ", cluster='" + cluster + '\'' +
                ", port='" + port + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", database='" + database + '\'' +
                ", table='" + table + '\'' +
                ", shardingColumn='" + shardingColumn + '\'' +
                '}';
    }

    public String getConnectUrl() {
        return connectUrl;
    }

    public String getCluster() {
        return cluster;
    }

    public String getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public String getTable() {
        return table;
    }

    public String getShardingColumn() {
        return shardingColumn;
    }
}
