package com.kuzetech.bigdata.spark.clickhouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.clickhouse.BalancedClickhouseDataSource;
import ru.yandex.clickhouse.ClickHouseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestBalancedConnect {

    private final static Logger logger = LoggerFactory.getLogger(TestBalancedConnect.class);

    public static void main(String[] args) {

        String url = "jdbc:clickhouse://localhost:8121,localhost:8122/system?log_queries=1";
        String sql = "select shard_num, any(host_name) as host_name from clusters where cluster = 'my' group by shard_num";

        BalancedClickhouseDataSource balancedDs = new BalancedClickhouseDataSource(url)
                .scheduleActualization(5000, TimeUnit.MILLISECONDS);

        Map<String, String> map = new HashMap<>();

        try(ClickHouseConnection conn = balancedDs.getConnection("default", "")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String shardNum = rs.getString("shard_num");
                String host = rs.getString("host_name");
                map.put(shardNum, host);
            }
            System.out.println(map.toString());
        } catch (SQLException e) {
            logger.error("执行语句出现错误，详细信息为：{}", e.getMessage());
        }
    }
}
