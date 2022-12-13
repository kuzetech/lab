package com.kuze.bigdata.lab;

import com.clickhouse.jdbc.*;
import java.sql.*;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws Exception {
        String url = "jdbc:ch://localhost:8123/default";
        Properties properties = new Properties();

        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);

        try (Connection connection = dataSource.getConnection("default", "");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO action (uid, event, time) VALUES (?, ?, ?)")) {

            // 如果 insert 语句中包涵字段，则必须 set 值
            // 如果该字段允许为空，则可以 setObject null
            // 否则报错 java.sql.SQLException: Cannot set null to non-nullable column #2 [event String]
            statement.setObject(1, 9);
            statement.setObject(2, "测试");
            statement.setObject(3, "2020-01-02 12:10:00");
            statement.addBatch();
            statement.executeBatch();
        }

        try (Connection connection = dataSource.getConnection("default", "");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO action (uid, time) VALUES (?, ?)")) {
            // 如果 insert 语句中不指定 event 字段
            // 插入时由于该字段不能为空，会采用默认值插入表中，比如 event 字段为 string 类型，默认值则为 ""（空字符串）
            statement.setObject(1, 9);
            statement.setObject(2, "2020-01-02 12:10:00");
            statement.addBatch();
            statement.executeBatch();
        }

        // 执行 select 语句
        try (Connection connection = dataSource.getConnection("default", "");
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("select * from action where uid > 6 limit 10")) {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columns = resultSetMetaData.getColumnCount();
            while (resultSet.next()) {
                for (int c = 1; c <= columns; c++) {
                    System.out.print(resultSetMetaData.getColumnName(c) + ":" + resultSet.getString(c) + (c < columns ? ", " : "\n"));
                }
            }
        }


    }
}
