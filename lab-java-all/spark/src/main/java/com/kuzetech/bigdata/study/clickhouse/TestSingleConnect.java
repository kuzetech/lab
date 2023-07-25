package com.kuzetech.bigdata.study.clickhouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class TestSingleConnect {

    private final static Logger logger = LoggerFactory.getLogger(TestSingleConnect.class);

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
        String url = "jdbc:clickhouse://localhost:8121/system";
        String sql = "select host_name from clusters where cluster = 'my'";

        Connection conn = DriverManager.getConnection(url, "default", "");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        logger.info(rs.toString());
    }
}
