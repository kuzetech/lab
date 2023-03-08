package com.kuze.bigdata.lab;

import com.clickhouse.jdbc.ClickHouseDataSource;

import java.sql.*;

/**
 * Hello world!
 *
 */
public class TestSelect
{
    public static void main(String[] args) throws Exception {
        ClickHouseDataSource dataSource = Client.CreateClickHouseDataSource();

        try (
                Connection connection = dataSource.getConnection("default", "");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from action where uid > 0 limit 10")
        ) {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnNum = resultSetMetaData.getColumnCount();
            while (resultSet.next()) {
                for (int c = 1; c <= columnNum; c++) {
                    System.out.print(resultSetMetaData.getColumnName(c) + ":" + resultSet.getString(c) + (c < columnNum ? ", " : "\n"));
                }
            }
        }
    }
}
