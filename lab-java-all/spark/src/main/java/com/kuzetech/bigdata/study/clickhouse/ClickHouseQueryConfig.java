package com.kuzetech.bigdata.study.clickhouse;

import com.kuzetech.bigdata.study.streaming.udsink.CanBeEmptyString;
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap;

import java.io.Serializable;
import java.lang.reflect.Field;

public class ClickHouseQueryConfig implements Serializable {

    // 该类暂时只能支持 String 类型的参数

    private String connectUrl;
    private String cluster;
    private String port;
    private String user;
    @CanBeEmptyString
    private String password;
    private String database;
    private String table;
    private String shardingColumn;
    private String walLocation;

    public ClickHouseQueryConfig( CaseInsensitiveMap<String> optionMap) {
        this.connectUrl = optionMap.get("connectUrl").isEmpty() ? "" : optionMap.get("connectUrl").get();
        this.cluster = optionMap.get("cluster").isEmpty() ? "" : optionMap.get("cluster").get();
        this.port = optionMap.get("port").isEmpty() ? "" : optionMap.get("port").get();
        this.user = optionMap.get("user").isEmpty() ? "" : optionMap.get("user").get();
        this.password = optionMap.get("password").isEmpty() ? "" : optionMap.get("password").get();
        this.database = optionMap.get("database").isEmpty() ? "" : optionMap.get("database").get();
        this.table = optionMap.get("table").isEmpty() ? "" : optionMap.get("table").get();
        this.shardingColumn = optionMap.get("shardingColumn").isEmpty() ? "" : optionMap.get("shardingColumn").get();
        this.walLocation = optionMap.get("walLocation").isEmpty() ? "" : optionMap.get("walLocation").get();
        this.checkParameters();
    }

    private void checkParameters() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            String value;
            try {
                value = (String) field.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException("ClickHouseQueryConfig " + field.getName() + " IllegalAccessException");
            }

            CanBeEmptyString annotation = field.getAnnotation(CanBeEmptyString.class);
            if (annotation == null && value == "") {
                throw new RuntimeException("ClickHouseQueryConfig " + field.getName() + " 不能为空");
            }
        }
    }


    public String getConnectUrl() {
        return connectUrl;
    }

    public void setConnectUrl(String connectUrl) {
        this.connectUrl = connectUrl;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getShardingColumn() {
        return shardingColumn;
    }

    public void setShardingColumn(String shardingColumn) {
        this.shardingColumn = shardingColumn;
    }

    public String getWalLocation() {
        return walLocation;
    }

    public void setWalLocation(String walLocation) {
        this.walLocation = walLocation;
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
                ", walLocation='" + walLocation + '\'' +
                '}';
    }
}
