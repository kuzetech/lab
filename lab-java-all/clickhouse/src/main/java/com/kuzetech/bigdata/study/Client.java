package com.kuzetech.bigdata.study;

import com.clickhouse.jdbc.ClickHouseDataSource;

import java.sql.SQLException;
import java.util.Properties;

public class Client {

    private static final String CONNECT_URL = "jdbc:ch://localhost:8123/default";

    public static ClickHouseDataSource CreateClickHouseDataSource() throws SQLException {
        Properties properties = new Properties();
        // properties.put("", "");
        ClickHouseDataSource dataSource = new ClickHouseDataSource(CONNECT_URL, properties);
        return dataSource;
    }

}
