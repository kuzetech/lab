package com.kuze.bigdata.lab;

import com.clickhouse.jdbc.ClickHouseDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Hello world!
 *
 */
public class TestBatchInsert
{
    public static void main(String[] args) throws Exception {
        ClickHouseDataSource dataSource = Client.CreateClickHouseDataSource();

        try (
                Connection connection = dataSource.getConnection("default", "");
                PreparedStatement statement = connection.prepareStatement("INSERT INTO action (uid, event, time) VALUES (?, ?, ?)")
        ) {

            // 如果 insert 语句中指定了问号占位符，则必须 set 值，
            // 即使该字段允许为空，也必须 setObject null，否则会报错 java.sql.SQLException: Cannot set null to non-nullable column #2 [event String]

            // 如果 insert 语句中未指定不能为空字段
            // 语句可以正常运行，clickhouse 插入时会自动填充默认值

            statement.setObject(1, 9);
            statement.setObject(2, "测试");
            statement.setObject(3, "2020-01-02 12:10:00");

            // parameters will be write into buffered stream immediately in binary format
            statement.addBatch();

            // stream everything on-hand into ClickHouse
            statement.executeBatch();
        }
    }
}
