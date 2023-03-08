package com.kuze.bigdata.lab;

import com.clickhouse.jdbc.ClickHouseDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Hello world!
 *
 */
public class TestBatchInsertDeduplicate
{
    public static void main(String[] args) throws Exception {
        ClickHouseDataSource dataSource = Client.CreateClickHouseDataSource();

        try (
                Connection connection = dataSource.getConnection("default", "");
                PreparedStatement statement = connection.prepareStatement("INSERT INTO action (uid, event, time) SETTINGS insert_deduplication_token = 'test' VALUES (?, ?, ?)")
        ) {
            statement.setObject(1, 2);
            statement.setObject(2, "test1");
            statement.setObject(3, "2020-01-02 12:10:00");
            statement.addBatch();
            statement.setObject(1, 2);
            statement.setObject(2, "test3");
            statement.setObject(3, "2020-01-02 12:13:00");
            statement.addBatch();

            statement.executeBatch();
        }
    }
}
