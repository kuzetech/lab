package com.kuze.bigdata.study.clickhouse;

import com.kuze.bigdata.study.streaming.udsink.CanBeEmptyString;
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
    private String fsParentLocation;

    public ClickHouseQueryConfig( CaseInsensitiveMap<String> optionMap) {
        this.connectUrl = optionMap.get("connectUrl").isEmpty() ? "" : optionMap.get("connectUrl").get();
        this.cluster = optionMap.get("cluster").isEmpty() ? "" : optionMap.get("cluster").get();
        this.port = optionMap.get("port").isEmpty() ? "" : optionMap.get("port").get();
        this.user = optionMap.get("user").isEmpty() ? "" : optionMap.get("user").get();
        this.password = optionMap.get("password").isEmpty() ? "" : optionMap.get("password").get();
        this.database = optionMap.get("database").isEmpty() ? "" : optionMap.get("database").get();
        this.table = optionMap.get("table").isEmpty() ? "" : optionMap.get("table").get();
        this.shardingColumn = optionMap.get("shardingColumn").isEmpty() ? "" : optionMap.get("shardingColumn").get();
        this.fsParentLocation = optionMap.get("fsParentLocation").isEmpty() ? "" : optionMap.get("fsParentLocation").get();
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


    public String generateCompleteWalUrl() {
        String url = this.fsParentLocation + "/wal";
        return url;
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
}
